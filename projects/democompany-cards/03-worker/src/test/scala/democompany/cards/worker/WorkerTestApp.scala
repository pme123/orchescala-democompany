// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.cards.worker

// sbt worker/test:run
object WorkerTestApp extends CompanyWorkerApp:
  workers(
    
  )
  dependencies(
    WorkerApp,
    democompany.services.worker.WorkerApp
  )
end WorkerTestApp