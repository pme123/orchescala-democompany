package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineOpConfig
import orchescala.engine.rest.OAuthConfig
import orchescala.worker.op.OAuth2PasswordWorkerClient

import scala.concurrent.duration.*

trait CompanyOpClient extends OAuth2PasswordWorkerClient:

  lazy val operatonRestUrl: String                 = CompanyEngineOpConfig.operatonRestUrl
  lazy val oAuthConfig: OAuthConfig.PasswordGrant = CompanyEngineOpConfig.adminPasswordGrant

object CompanyOpClient extends CompanyOpClient

