package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineOperatonConfig
import orchescala.engine.rest.OAuthConfig
import orchescala.worker.operaton.OAuth2PasswordWorkerClient

import scala.concurrent.duration.*

trait CompanyOpClient extends OAuth2PasswordWorkerClient:

  lazy val operatonRestUrl: String                 = CompanyEngineOperatonConfig.operatonRestUrl
  lazy val oAuthConfig: OAuthConfig.PasswordGrant = CompanyEngineOperatonConfig.adminPasswordGrant

object CompanyOpClient extends CompanyOpClient

