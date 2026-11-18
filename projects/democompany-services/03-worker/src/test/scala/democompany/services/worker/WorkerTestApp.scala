// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.services.worker

// sbt worker/Test/run
object WorkerTestApp extends CompanyWorkerApp:
  workers(
    
  )
  dependencies(
    WorkerApp,
    
  )
end WorkerTestApp