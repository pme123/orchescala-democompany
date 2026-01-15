// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.cards.worker

// sbt worker/test:run
object WorkerTestApp extends CompanyWorkerApp:
  workers(
    
  )
  dependencies(
    WorkerApp,
  //  democompany.services.worker.WorkerApp
  )
end WorkerTestApp