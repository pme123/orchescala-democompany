// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.patterns.worker

import orchescala.worker.c7.C7WorkerRegistry
import orchescala.worker.c8.C8WorkerRegistry

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    initProcessWorkers
  )
  dependencies(
  )

  // the pattern examples run on C7 and C8 - C8 on a local c8run (see PatternsC8Client)
  override lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(C7WorkerRegistry(CompanyC7Client), C8WorkerRegistry(PatternsC8Client))

  // WORKER_APP_PORT - if 5555 is taken by another worker app
  override def port: Int =
    sys.env.get("WORKER_APP_PORT").map(_.toInt).getOrElse(super.port)

  private lazy val initProcessWorkers =
    import democompany.patterns.worker.initProcess.v1.*
    Seq(
      new InitProcessWorker()
    )
  end initProcessWorkers
end WorkerApp
