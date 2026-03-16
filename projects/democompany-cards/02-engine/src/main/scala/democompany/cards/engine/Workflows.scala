package democompany.cards.engine

import cats.effect.{IO, Resource}
import io.circe.Encoder
import workflows4s.runtime.instanceengine.WorkflowInstanceEngine
import workflows4s.runtime.{InMemoryRuntime, InMemorySyncRuntime, WorkflowRuntime}
import workflows4s.web.api.server.{SignalSupport, WorkflowEntry}

object Workflows:

  /** Builds the list of workflow entries using the given engine.
   *
   *  The engine (and its associated knocker-upper / registry) is created once
   *  by the caller (WorkerApp.apiRoutes) so that all workflow instances share
   *  the same registry – which is required for the search feature to work.
   */
  def workflows(engineResource: Resource[IO,WorkflowInstanceEngine]): Resource[IO, List[WorkflowEntry[IO, ?]]] =
    for
      engine <- engineResource
      exampleRuntime <- InMemoryRuntime
                          .default[MyWorkflowCtx.Ctx](
                            workflow = Workflow.workflow,
                            initialState = PRState.Empty,
                            engine = engine
                          )
                          .toResource
      orderCreditcardRuntime <- InMemoryRuntime
                          .default[OrderCreditcardV1Ctx.Ctx](
                            workflow = OrderCreditcardV1.workflow,
                            initialState = OrderCreditcardState.Empty,
                            engine = engine
                          )
                          .toResource
    yield List[WorkflowEntry[IO, ?]](
      WorkflowEntry(
        name = "Example Workflow",
        description =
          Some("Example workflow demonstrating a simple ......"),
        runtime = exampleRuntime,
        stateEncoder = summon[Encoder[PRState]],
        signalSupport = SignalSupport.builder
          .add(PRSignals.createPR)
          .add(PRSignals.reviewPR)
          .build
      ),
      WorkflowEntry(
        name = "Order Creditcard V1",
        description =
          Some("Workflow for ordering a credit card with client validation and approval checks"),
        runtime = orderCreditcardRuntime,
        stateEncoder = summon[Encoder[OrderCreditcardState]],
        signalSupport = SignalSupport.builder
          .add(OrderCreditcardSignals.startProcess)
          .add(OrderCreditcardSignals.checkOrder)
          .add(OrderCreditcardSignals.receiveEmail)
          .add(OrderCreditcardSignals.callClient)
          .add(OrderCreditcardSignals.cancelOrder)
          .build
      )
    )

  def main(args: Array[String]): Unit =
    val engine     = WorkflowInstanceEngine.basic()
    val runtime    = InMemorySyncRuntime.create[MyWorkflowCtx.Ctx](Workflow.workflow, PRState.Empty, engine)
    val wfInstance = runtime.createInstance("test-instance-d1fc1fd2-b671-4ac3-9c2b-b53797bcf749")

    wfInstance.deliverSignal(
          PRSignals.createPR,
          PRSignals.CreateRequest("test-instance-d1fc1fd2-b671-4ac3-9c2b-b53797bcf749")
    )
    println(wfInstance.queryState())
    // Checked(some-sha,<Some tests results>)
  end main

  //  wfInstance.deliverSignal(PRSignals.reviewPR, PRSignals.ReviewRequest(approve = false))
  //  println(wfInstance.queryState())
end Workflows
