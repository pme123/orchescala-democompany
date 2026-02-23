package democompany.orchescala.worker

import orchescala.engine.domain.EngineType
import orchescala.worker.c7.C7WorkerRegistry
import orchescala.worker.c8.C8WorkerRegistry

trait CompanyWorkerApp extends WorkerApp:
  lazy val workerConfig  = companyWorkerConfig
  lazy val engineContext = CompanyEngineC8Context(CompanyRestApiC8Client())

  lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(
     // C7WorkerRegistry(CompanyC7Client),
     // C8WorkerRegistry(CompanyC8Client),
      C7WorkerRegistry(CompanyOpClient, EngineType.Op)
    )

end CompanyWorkerApp
