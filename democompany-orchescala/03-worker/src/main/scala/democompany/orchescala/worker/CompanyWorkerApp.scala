package democompany.orchescala.worker

import democompany.orchescala.engine.companyEngineTypes
import orchescala.engine.domain.EngineType
import orchescala.worker.c7.C7WorkerRegistry
import orchescala.worker.c8.C8WorkerRegistry
import orchescala.worker.op.OpWorkerRegistry

trait CompanyWorkerApp extends WorkerApp:
  lazy val workerConfig = companyWorkerConfig

  lazy val engineContext = CompanyEngineC8Context(CompanyRestApiC8Client())

  // the workers register on the engines of COMPANY_ENGINES (default C7, C8 and Op)
  lazy val workerRegistries: Seq[WorkerRegistry] =
    companyEngineTypes.collect:
      case EngineType.C7 => C7WorkerRegistry(CompanyC7Client)
      case EngineType.C8 => C8WorkerRegistry(CompanyC8Client)
      case EngineType.Op => OpWorkerRegistry(CompanyOpClient)

end CompanyWorkerApp
