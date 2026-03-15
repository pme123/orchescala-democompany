package democompany.cards.worker

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.server.middleware.CORS
import sttp.tapir.server.http4s.Http4sServerInterpreter
import workflows4s.runtime.instanceengine.WorkflowInstanceEngine
import workflows4s.runtime.registry.InMemoryWorkflowRegistry
import workflows4s.runtime.wakeup.SleepingKnockerUpper
import workflows4s.web.api.server.{WorkflowEntry, WorkflowServerEndpoints}
import democompany.cards.engine.Workflows

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:

  // Not called – apiRoutes is overridden below.
  def workflowEntries: Resource[cats.effect.IO, List[WorkflowEntry[cats.effect.IO, ?]]] =
      Workflows.workflows(w4SEngine)

  workers(
    orderCreditcardWorkers,
  )
  dependencies(
  )

  private lazy val orderCreditcardWorkers =
    import democompany.cards.worker.orderCreditcard.v1.*
    Seq(
      new OrderCreditcardWorker(),
      new EvalResponseWorker()
    )
  end orderCreditcardWorkers
end WorkerApp
