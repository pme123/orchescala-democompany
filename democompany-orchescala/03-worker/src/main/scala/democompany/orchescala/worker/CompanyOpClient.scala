package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineOpConfig
import orchescala.engine.domain.EngineType
import orchescala.worker.c7.C7NoAuthWorkerClient

trait CompanyOpClient extends C7NoAuthWorkerClient:

  override protected lazy val engineType: EngineType = EngineType.Op
  override protected lazy val camundaRestUrl: String = CompanyEngineOpConfig.operatonRestUrl

object CompanyOpClient extends CompanyOpClient
