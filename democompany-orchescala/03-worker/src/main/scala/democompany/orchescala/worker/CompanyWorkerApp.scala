package democompany.orchescala.worker

import orchescala.engine.w4s.{DefaultW4SConfig, W4SServer}
import orchescala.worker.c7.C7WorkerRegistry

trait CompanyWorkerApp extends WorkerApp with W4SServer:

  lazy val w4SConfig = DefaultW4SConfig()
  lazy val workerConfig = companyWorkerConfig

  lazy val engineContext = CompanyEngineC8Context(CompanyRestApiC8Client())

  lazy val workerRegistries: Seq[WorkerRegistry] =
    Seq(C7WorkerRegistry(CompanyC7Client)/*, C8WorkerRegistry(CompanyC8Client))*/)//, OpWorkerRegistry(CompanyOpClient), W4SWorkerRegistry(CompanyW4SClient))

  override protected def additionalServices: ZIO[Any, Any, Any] =
    serverWithUiZIO

end CompanyWorkerApp
