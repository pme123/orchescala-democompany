package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config
import orchescala.engine.rest.OAuthConfig
import orchescala.worker.c7.OAuth2PasswordWorkerClient

import scala.concurrent.duration.*

trait CompanyC7Client extends OAuth2PasswordWorkerClient:

  lazy val camundaRestUrl: String                 = CompanyEngineC7Config.camundaRestUrl
  lazy val oAuthConfig: OAuthConfig.PasswordGrant = CompanyEngineC7Config.adminPasswordGrant

object CompanyC7Client extends CompanyC7Client
