package democompany.orchescala.worker

import orchescala.worker.c7.C7WorkerRegistry

trait CompanyWorkerApp extends WorkerApp:

  lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(C7WorkerRegistry(CompanyC7Client))
