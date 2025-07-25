package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC8Config
import orchescala.worker.c8.C8SaasWorkerClient

trait CompanyC8Client extends C8SaasWorkerClient:
  lazy val zeebeGrpc = CompanyEngineC8Config.zeebeGrpc
  lazy val zeebeRest = CompanyEngineC8Config.zeebeRest
  lazy val audience = CompanyEngineC8Config.audience
  lazy val clientId = CompanyEngineC8Config.clientId
  lazy val clientSecret = CompanyEngineC8Config.clientSecret
  lazy val oAuthAPI: String = CompanyEngineC8Config.oAuthAPI

end CompanyC8Client

object CompanyC8Client extends CompanyC8Client 