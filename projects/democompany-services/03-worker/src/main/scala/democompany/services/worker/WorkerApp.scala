// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.services.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    clientsV1Workers
  )
  dependencies(
  )

  private lazy val clientsV1Workers = Seq(
    clients.v1.GetClientsClientIdWorker()
  )
end WorkerApp
