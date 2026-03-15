package democompany.cards.engine

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.camunda.bpm.model.bpmn.Bpmn
import sttp.tapir.Schema
import workflows4s.bpmn.BpmnRenderer
import workflows4s.wio.DraftWorkflowContext.*
import workflows4s.wio.{SignalDef, WorkflowContext}

import java.io.File


object Workflow:
  import MyWorkflowCtx.*

  def main(args: Array[String]): Unit =
    val bpmnModel = BpmnRenderer.renderWorkflow(workflow.toProgress.toModel, "process")

    Bpmn.writeModelToFile(new File(s"pr-draft.bpmn").getAbsoluteFile, bpmnModel)

  lazy val workflow: WIO.Initial = (
    createPR >>>
      runPipeline >>>
      processReview >>>
      mergePR
    ).handleErrorWith(closePR)

  lazy val createPR: WIO[Any, PRError.CommitNotFound.type, PRState.Initiated] =
    WIO
      .handleSignal(Signals.createPR)
      .using[Any]
      .purely((in, req) => PREvent.Created(req.commit))
      .handleEventWithError((in, evt) =>
        if evt.commit.length > 8 then Left(PRError.CommitNotFound)
        else Right(PRState.Initiated(evt.commit)),
      )
      .voidResponse
      .autoNamed

  lazy val runPipeline: WIO[PRState.Initiated, PRError.PipelineFailed.type, PRState.Checked] =
    WIO
      .runIO[PRState.Initiated](in => IO(PREvent.Checked("<Some tests results>")))
      .handleEventWithError((in, evt) =>
        if evt.pipelineResults.contains("error") then Left(PRError.PipelineFailed)
        else Right(PRState.Checked(in.commit, evt.pipelineResults)),
      )
      .autoNamed()

  lazy val processReview: WIO[PRState.Checked, PRError.ReviewRejected.type, PRState.Reviewed] =
    WIO
      .handleSignal(Signals.reviewPR)
      .using[PRState.Checked]
      .purely((in, req) => PREvent.Reviewed(req.approve))
      .handleEventWithError((in, evt) =>
        if evt.approved then Right(PRState.Reviewed(in.commit, in.pipelineResults, evt.approved))
        else Left(PRError.ReviewRejected),
      )
      .voidResponse
      .autoNamed
      
  lazy val mergePR: WIO[PRState.Reviewed, Nothing, PRState.Merged] =
    WIO.pure.makeFrom[PRState.Reviewed].value(identity).autoNamed
  lazy val closePR: WIO[(PRState, PRError), Nothing, PRState.Closed] =
    WIO.pure.makeFrom[(PRState, PRError)].value((state, err) => PRState.Closed(state, err)).autoNamed

end Workflow

sealed trait PRState derives Encoder

object PRState:
  case object Empty extends PRState

  case class Initiated(commit: String) extends PRState

  case class Checked(commit: String, pipelineResults: String) extends PRState

  case class Reviewed(commit: String, pipelineResults: String, approved: Boolean) extends PRState

  type Merged = Reviewed

  case class Closed(state: PRState, reason: PRError) extends PRState


sealed trait PREvent

object PREvent:
  case class Created(commit: String) extends PREvent

  case class Checked(pipelineResults: String) extends PREvent

  case class Reviewed(approved: Boolean) extends PREvent

object Signals {
  val createPR: SignalDef[CreateRequest, Unit] = SignalDef()
  val reviewPR: SignalDef[ReviewRequest, Unit] = SignalDef()

  case class CreateRequest(commit: String) derives Schema, Decoder

  case class ReviewRequest(approve: Boolean) derives Schema, Decoder
}

object MyWorkflowCtx extends WorkflowContext {
  override type Event = PREvent
  override type State = PRState
}

sealed trait PRError derives Encoder

object PRError {
  case object CommitNotFound extends PRError

  case object PipelineFailed extends PRError

  case object ReviewRejected extends PRError
}
  
  
