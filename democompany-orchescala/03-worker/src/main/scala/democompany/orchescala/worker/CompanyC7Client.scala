package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineConfig
import orchescala.worker.c7.OAuth2WorkerClient
import scala.concurrent.duration.*

trait CompanyC7Client extends OAuth2WorkerClient:
  lazy val fssoRealm = CompanyEngineConfig.fssoRealm
  lazy val fssoBaseUrl = CompanyEngineConfig.fssoBaseUrl
  override lazy val camundaRestUrl = CompanyEngineConfig.camundaRestUrl
  override lazy val client_id = CompanyEngineConfig.fssoClientName
  override lazy val client_secret = CompanyEngineConfig.fssoClientSecret
  override lazy val scope = CompanyEngineConfig.fssoScope
  override lazy val username = CompanyEngineConfig.fssoTechuserName
  override lazy val password = CompanyEngineConfig.fssoTechuserPassword

  override lazy val lockDuration: Long = 5.minutes.toMillis

end CompanyC7Client

object CompanyC7Client extends CompanyC7Client
