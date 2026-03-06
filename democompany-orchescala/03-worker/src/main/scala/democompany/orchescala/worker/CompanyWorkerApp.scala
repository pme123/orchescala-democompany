package democompany.orchescala.worker

import orchescala.worker.c7.C7WorkerRegistry
import orchescala.worker.c8.C8WorkerRegistry
import orchescala.worker.op.OpWorkerRegistry
import orchescala.worker.w4s.W4SWorkerRegistry

trait CompanyWorkerApp extends WorkerApp:
  lazy val workerConfig = companyWorkerConfig

  lazy val engineContext = CompanyEngineC8Context(CompanyRestApiC8Client())

  lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(C7WorkerRegistry(CompanyC7Client)/*, C8WorkerRegistry(CompanyC8Client))*/, OpWorkerRegistry(CompanyOpClient), W4SWorkerRegistry(CompanyW4SClient))

end CompanyWorkerApp
