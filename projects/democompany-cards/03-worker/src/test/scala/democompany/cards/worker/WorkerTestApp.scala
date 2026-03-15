// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.cards.worker

import cats.effect
import cats.effect.Resource
import democompany.cards.engine.Workflows
import orchescala.engine.w4s.W4SConfig
import workflows4s.web.api.server.WorkflowEntry

// sbt worker/test:run
object WorkerTestApp extends CompanyWorkerApp:
  workers(
    
  )
  dependencies(
    WorkerApp,
    democompany.services.worker.WorkerApp
  )
  
  protected def workflowEntries: Resource[cats.effect.IO, List[WorkflowEntry[cats.effect.IO, ?]]] = 
    WorkerApp.workflowEntries
end WorkerTestApp