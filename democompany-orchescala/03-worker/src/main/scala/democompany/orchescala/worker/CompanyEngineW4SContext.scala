package democompany.orchescala.worker

import democompany.orchescala.engine.companyEngineConfig
import orchescala.engine.EngineConfig
import orchescala.worker.w4s.W4SContext

class CompanyEngineW4SContext extends W4SContext:
  def engineConfig: EngineConfig = companyEngineConfig
  def workerConfig: WorkerConfig = companyWorkerConfig

