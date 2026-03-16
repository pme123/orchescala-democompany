package democompany.cards.engine

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.camunda.bpm.model.bpmn.Bpmn
import sttp.tapir.Schema
import workflows4s.bpmn.BpmnRenderer
import workflows4s.wio.{SignalDef, WorkflowContext}

import java.io.File
import java.time.{Duration, Instant}
import scala.util.Try

object OrderCreditcardV1:
  import OrderCreditcardV1Ctx.*

  def main(args: Array[String]): Unit =
    val bpmnModel = BpmnRenderer.renderWorkflow(workflow.toProgress.toModel, "process")

    Bpmn.writeModelToFile(new File(s"orderCreditcard-draft.bpmn").getAbsoluteFile, bpmnModel)

  private val DefaultEmailTimeout = Duration.parse("P3D")

  lazy val workflow: WIO.Initial = (
    initProcess >>>
      getClient >>>
      checkOrder >>>
      routeAfterCheck
    ).handleErrorWith(handleError)

  lazy val initProcess: WIO[Any, OrderCreditcardError.OutputMocked.type, OrderCreditcardState.Initialized] =
    WIO
      .handleSignal(OrderCreditcardSignals.startProcess)
      .using[Any]
      .purely((_, req) => OrderCreditcardEvent.ProcessInitialized(req))
      .handleEventWithError((_, evt) =>
        Either.cond(
          !evt.request.outputMocked,
          OrderCreditcardState.Initialized(evt.request),
          OrderCreditcardError.OutputMocked,
        )
      )
      .voidResponse
      .autoNamed

  lazy val getClient: WIO[OrderCreditcardState.Initialized, OrderCreditcardError.ClientNotFound.type, OrderCreditcardState.ReadyToCheckOrder] =
    WIO
      .runIO[OrderCreditcardState.Initialized](in =>
        in.request.clientLookupErrorCode match
          case Some(444) => IO.raiseError(RuntimeException("444: Service Error: 444\nErrorMsg: Mocked Error:"))
          case other     =>
            IO.pure(
              OrderCreditcardEvent.ClientFetched(
                clientData = in.request.clientData,
                email = in.request.clientEmail,
                errorCode = other,
              )
            )
      )
      .handleEventWithError((in, evt) =>
        evt.errorCode match
          case Some(404) => Left(OrderCreditcardError.ClientNotFound)
          case _         =>
            Right(
              OrderCreditcardState.ReadyToCheckOrder(
                request = in.request,
                clientData = evt.clientData,
                email = evt.email,
              )
            )
      )
      .autoNamed()

  lazy val checkOrderBase: WIO[OrderCreditcardState.ReadyToCheckOrder, Nothing, OrderCreditcardState.PostCheck] =
    WIO
      .handleSignal(OrderCreditcardSignals.checkOrder)
      .using[OrderCreditcardState.ReadyToCheckOrder]
      .purely((_, req) => OrderCreditcardEvent.OrderApprovalDecided(req.approved, req.approveComment))
      .handleEventWithError((in, evt) =>
        Right(
          OrderCreditcardState.CheckOrderCompleted(
            request = in.request,
            clientData = in.clientData,
            email = in.email,
            approved = evt.approved,
            approveComment = evt.approveComment,
          )
        )
      )
      .voidResponse
      .autoNamed

  lazy val cancelInterruption: workflows4s.wio.WIO.Interruption[OrderCreditcardV1Ctx.Ctx, Nothing, OrderCreditcardState.PostCheck] =
    new workflows4s.wio.builders.InterruptionBuilder.Step0[OrderCreditcardV1Ctx.Ctx]()
      .throughSignal(OrderCreditcardSignals.cancelOrder)
      .handleSync((in: OrderCreditcardState, req: OrderCreditcardSignals.CancelOrderRequest) =>
        OrderCreditcardEvent.OrderCanceled(req.reason)
      )
      .handleEvent((in, evt) =>
        in match
          case ready: OrderCreditcardState.ReadyToCheckOrder =>
            OrderCreditcardState.Canceled(
              request = ready.request,
              clientData = ready.clientData,
              email = ready.email,
              reason = evt.reason,
            )
          case other                                         =>
            throw new IllegalStateException(s"Cancel boundary is only valid while waiting in Check Order, got: ${other.getClass.getSimpleName}")
      )
      .voidResponse
      .autoNamed

  lazy val checkOrder: WIO[OrderCreditcardState.ReadyToCheckOrder, Nothing, OrderCreditcardState.PostCheck] =
    checkOrderBase
      .interruptWith(cancelInterruption)

  lazy val routeAfterCheck: WIO[OrderCreditcardState.PostCheck, Nothing, OrderCreditcardState] =
    WIO
      .fork[OrderCreditcardState.PostCheck]
      .onSome {
        case canceled: OrderCreditcardState.Canceled => Some(canceled)
        case _                                       => None
      }(endCanceled)
      .onSome {
        case checked: OrderCreditcardState.CheckOrderCompleted => Some(checked)
        case _                                                 => None
      }(routeApproval)
      .autoNamed()
      .done

  lazy val endCanceled: WIO[OrderCreditcardState.Canceled, Nothing, OrderCreditcardState] =
    WIO.pure.makeFrom[OrderCreditcardState.Canceled].value(identity).autoNamed

  lazy val routeApproval: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState] =
    WIO
      .fork[OrderCreditcardState.CheckOrderCompleted]
      .matchCondition(_.approved, "approved?")(
        approvedFlow,
        notApprovedFlow,
      )

  lazy val approvedFlow: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState] =
    orderCard >>> succeed

  lazy val notApprovedFlow: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState] =
    WIO
      .fork[OrderCreditcardState.CheckOrderCompleted]
      .matchCondition(_.email.nonEmpty, "has email?")(
        emailFlow,
        telephoneFlow,
      )

  lazy val emailFlow: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState] =
    sendEmail >>> waitForEmailResponse >>> evalResponse >>> routeEvaluation

  lazy val telephoneFlow: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState] =
    prepareCallClient >>> callClient >>> evalResponse >>> routeEvaluation

  lazy val sendEmail: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState.WaitingForClientResponse] =
    WIO
      .pure
      .makeFrom[OrderCreditcardState.CheckOrderCompleted]
      .value(in =>
        OrderCreditcardState.WaitingForClientResponse(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
          approveComment = in.approveComment,
          timerNotReceivedEmail = in.request.timerNotReceivedEmail,
        )
      )
      .autoNamed

  lazy val waitForEmailBase: WIO[OrderCreditcardState.WaitingForClientResponse, Nothing, OrderCreditcardState.ClientResponse] =
    WIO
      .handleSignal(OrderCreditcardSignals.receiveEmail)
      .using[OrderCreditcardState.WaitingForClientResponse]
      .purely((_, req) => OrderCreditcardEvent.EmailReceived(req.body))
      .handleEvent((in, evt) =>
        OrderCreditcardState.EmailResponseReceived(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
          approveComment = in.approveComment,
          body = evt.body,
        )
      )
      .voidResponse
      .autoNamed

  lazy val emailTimeoutTimer: workflows4s.wio.WIO.Timer[OrderCreditcardV1Ctx.Ctx, OrderCreditcardState, Nothing, OrderCreditcardState] =
    WIO
      .AwaitBuilderStep1[OrderCreditcardState](
        workflows4s.wio.WIO.Timer.DurationSource.Dynamic {
          case waiting: OrderCreditcardState.WaitingForClientResponse => parseDuration(waiting.timerNotReceivedEmail)
          case _                                                      => DefaultEmailTimeout
        }
      )
      .persistStartThrough(started => OrderCreditcardEvent.EmailTimerStarted(started.at))(_.at)
      .persistReleaseThrough(released => OrderCreditcardEvent.EmailTimerElapsed(released.at))(_.at)
      .autoNamed

  lazy val emailTimeout: workflows4s.wio.WIO.Interruption[OrderCreditcardV1Ctx.Ctx, Nothing, OrderCreditcardState.ClientResponse] =
    emailTimeoutTimer
      .toInterruption
      .andThen(
        _.transformOutput((in, _) =>
          in match
            case waiting: OrderCreditcardState.WaitingForClientResponse =>
              OrderCreditcardState.EmailTimedOut(
                request = waiting.request,
                clientData = waiting.clientData,
                email = waiting.email,
              )
            case other                                                  =>
              throw new IllegalStateException(s"Email timeout is only valid while waiting for a client response, got: ${other.getClass.getSimpleName}")
        )
      )

  lazy val waitForEmailResponse: WIO[OrderCreditcardState.WaitingForClientResponse, Nothing, OrderCreditcardState.ClientResponse] =
    waitForEmailBase.interruptWith(
      emailTimeout
    )

  lazy val prepareCallClient: WIO[OrderCreditcardState.CheckOrderCompleted, Nothing, OrderCreditcardState.AwaitingPhoneCall] =
    WIO
      .pure
      .makeFrom[OrderCreditcardState.CheckOrderCompleted]
      .value(in =>
        OrderCreditcardState.AwaitingPhoneCall(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
          approveComment = in.approveComment,
        )
      )
      .autoNamed

  lazy val callClient: WIO[OrderCreditcardState.AwaitingPhoneCall, Nothing, OrderCreditcardState.ClientResponse] =
    WIO
      .handleSignal(OrderCreditcardSignals.callClient)
      .using[OrderCreditcardState.AwaitingPhoneCall]
      .purely((_, req) => OrderCreditcardEvent.TelephoneResponseReceived(req.approvedOnTelephone, req.approveOnTelephoneComment))
      .handleEvent((in, evt) =>
        OrderCreditcardState.TelephoneResponseReceived(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
          approveComment = in.approveComment,
          approvedOnTelephone = evt.approvedOnTelephone,
          approveOnTelephoneComment = evt.approveOnTelephoneComment,
        )
      )
      .voidResponse
      .autoNamed

  lazy val evalResponse: WIO[OrderCreditcardState.ClientResponse, Nothing, OrderCreditcardState.ResponseEvaluated] =
    WIO
      .runIO[OrderCreditcardState.ClientResponse](response =>
        IO.pure(OrderCreditcardEvent.ResponseEvaluated(isOrderCorrect(response)))
      )
      .handleEvent((in, evt) =>
        OrderCreditcardState.ResponseEvaluated(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
          orderCorrect = evt.orderCorrect,
        )
      )
      .autoNamed()

  lazy val routeEvaluation: WIO[OrderCreditcardState.ResponseEvaluated, Nothing, OrderCreditcardState] =
    WIO
      .fork[OrderCreditcardState.ResponseEvaluated]
      .matchCondition(_.orderCorrect, "order correct?")(
        orderCard >>> succeed,
        notSucceeded,
      )

  lazy val orderCard: WIO[OrderCreditcardState.Orderable, Nothing, OrderCreditcardState.CardOrdered] =
    WIO
      .pure
      .makeFrom[OrderCreditcardState.Orderable]
      .value(in =>
        OrderCreditcardState.CardOrdered(
          request = in.request,
          clientData = in.clientData,
          email = in.email,
        )
      )
      .autoNamed

  lazy val succeed: WIO[OrderCreditcardState.CardOrdered, Nothing, OrderCreditcardState.Succeeded] =
    WIO.pure.makeFrom[OrderCreditcardState.CardOrdered].value(OrderCreditcardState.Succeeded.apply).autoNamed

  lazy val notSucceeded: WIO[OrderCreditcardState.ResponseEvaluated, Nothing, OrderCreditcardState.NotSucceeded] =
    WIO.pure.makeFrom[OrderCreditcardState.ResponseEvaluated].value(OrderCreditcardState.NotSucceeded.apply).autoNamed

  lazy val handleError: WIO[(OrderCreditcardState, OrderCreditcardError), Nothing, OrderCreditcardState] =
    WIO
      .pure
      .makeFrom[(OrderCreditcardState, OrderCreditcardError)]
      .value {
        case (state, OrderCreditcardError.OutputMocked)  => OrderCreditcardState.OutputMocked(state)
        case (state, OrderCreditcardError.ClientNotFound) => OrderCreditcardState.Failed(state, OrderCreditcardError.ClientNotFound)
      }
      .autoNamed

  private def parseDuration(value: String): Duration =
    Try(Duration.parse(value)).getOrElse(DefaultEmailTimeout)

  private def isOrderCorrect(response: OrderCreditcardState.ClientResponse): Boolean =
    response match
      case _: OrderCreditcardState.EmailResponseReceived               => true
      case telephone: OrderCreditcardState.TelephoneResponseReceived   => telephone.approvedOnTelephone
      case _: OrderCreditcardState.EmailTimedOut                       => false

end OrderCreditcardV1

sealed trait OrderCreditcardState derives Encoder

object OrderCreditcardState:
  case object Empty extends OrderCreditcardState
  case class Initialized(request: OrderCreditcardSignals.StartProcessRequest) extends OrderCreditcardState derives Encoder
  case class ReadyToCheckOrder(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
  ) extends OrderCreditcardState derives Encoder

  sealed trait PostCheck extends OrderCreditcardState
  sealed trait Orderable extends OrderCreditcardState:
    def request: OrderCreditcardSignals.StartProcessRequest
    def clientData: String
    def email: Option[String]

  case class CheckOrderCompleted(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      approved: Boolean,
      approveComment: String,
  ) extends PostCheck,
        Orderable derives Encoder

  case class Canceled(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      reason: String,
  ) extends PostCheck derives Encoder

  case class WaitingForClientResponse(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      approveComment: String,
      timerNotReceivedEmail: String,
  ) extends OrderCreditcardState derives Encoder

  case class AwaitingPhoneCall(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      approveComment: String,
  ) extends OrderCreditcardState derives Encoder

  sealed trait ClientResponse extends OrderCreditcardState:
    def request: OrderCreditcardSignals.StartProcessRequest
    def clientData: String
    def email: Option[String]

  case class EmailResponseReceived(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      approveComment: String,
      body: String,
  ) extends ClientResponse derives Encoder

  case class EmailTimedOut(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
  ) extends ClientResponse derives Encoder

  case class TelephoneResponseReceived(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      approveComment: String,
      approvedOnTelephone: Boolean,
      approveOnTelephoneComment: String,
  ) extends ClientResponse derives Encoder

  case class ResponseEvaluated(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
      orderCorrect: Boolean,
  ) extends Orderable derives Encoder

  case class CardOrdered(
      request: OrderCreditcardSignals.StartProcessRequest,
      clientData: String,
      email: Option[String],
  ) extends OrderCreditcardState derives Encoder

  case class Succeeded(cardOrdered: CardOrdered) extends OrderCreditcardState derives Encoder
  case class NotSucceeded(response: ResponseEvaluated) extends OrderCreditcardState derives Encoder
  case class OutputMocked(state: OrderCreditcardState) extends OrderCreditcardState derives Encoder
  case class Failed(state: OrderCreditcardState, reason: OrderCreditcardError) extends OrderCreditcardState derives Encoder

sealed trait OrderCreditcardEvent

object OrderCreditcardEvent:
  case class ProcessInitialized(request: OrderCreditcardSignals.StartProcessRequest) extends OrderCreditcardEvent
  case class ClientFetched(clientData: String, email: Option[String], errorCode: Option[Int]) extends OrderCreditcardEvent
  case class OrderApprovalDecided(approved: Boolean, approveComment: String) extends OrderCreditcardEvent
  case class OrderCanceled(reason: String) extends OrderCreditcardEvent
  case class EmailReceived(body: String) extends OrderCreditcardEvent
  case class TelephoneResponseReceived(approvedOnTelephone: Boolean, approveOnTelephoneComment: String) extends OrderCreditcardEvent
  case class ResponseEvaluated(orderCorrect: Boolean) extends OrderCreditcardEvent
  case class EmailTimerStarted(at: Instant) extends OrderCreditcardEvent
  case class EmailTimerElapsed(at: Instant) extends OrderCreditcardEvent

sealed trait OrderCreditcardError derives Encoder

object OrderCreditcardError:
  case object ClientNotFound extends OrderCreditcardError
  case object OutputMocked extends OrderCreditcardError

object OrderCreditcardSignals:
  val startProcess: SignalDef[StartProcessRequest, Unit] = SignalDef()
  val checkOrder: SignalDef[CheckOrderRequest, Unit] = SignalDef()
  val receiveEmail: SignalDef[ReceiveEmailRequest, Unit] = SignalDef()
  val callClient: SignalDef[CallClientRequest, Unit] = SignalDef()
  val cancelOrder: SignalDef[CancelOrderRequest, Unit] = SignalDef()

  case class StartProcessRequest(
      clientId: String,
      clientData: String = "client-data",
      clientEmail: Option[String] = Some("client@example.com"),
      timerNotReceivedEmail: String = "P3D",
      outputMocked: Boolean = false,
      clientLookupErrorCode: Option[Int] = None,
  ) derives Schema,
        Decoder,
        Encoder

  case class CheckOrderRequest(
      approved: Boolean,
      approveComment: String = "Approved with no issues.",
  ) derives Schema,
        Decoder,
        Encoder

  case class ReceiveEmailRequest(
      body: String,
  ) derives Schema,
        Decoder,
        Encoder

  case class CallClientRequest(
      approvedOnTelephone: Boolean,
      approveOnTelephoneComment: String,
  ) derives Schema,
        Decoder,
        Encoder

  case class CancelOrderRequest(
      reason: String = "cancel Process",
  ) derives Schema,
        Decoder,
        Encoder

object OrderCreditcardV1Ctx extends WorkflowContext:
  override type Event = OrderCreditcardEvent
  override type State = OrderCreditcardState

