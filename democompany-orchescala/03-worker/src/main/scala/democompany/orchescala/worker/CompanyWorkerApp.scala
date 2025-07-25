package democompany.orchescala.worker

import orchescala.worker.c7.C7WorkerRegistry
import orchescala.worker.c8.C8WorkerRegistry

trait CompanyWorkerApp extends WorkerApp:

  lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(C7WorkerRegistry(CompanyC7Client), C8WorkerRegistry(CompanyC8Client))

end CompanyWorkerApp
