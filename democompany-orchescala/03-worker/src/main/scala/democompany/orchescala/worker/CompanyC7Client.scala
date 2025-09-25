package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config
import orchescala.worker.c7.OAuth2WorkerClient
import scala.concurrent.duration.*

trait CompanyC7Client extends OAuth2WorkerClient:
  lazy val fssoRealm = CompanyEngineC7Config.fssoRealm
  lazy val fssoBaseUrl = CompanyEngineC7Config.fssoBaseUrl
  override lazy val camundaRestUrl = CompanyEngineC7Config.camundaRestUrl
  override lazy val client_id = CompanyEngineC7Config.fssoClientName
  override lazy val client_secret = CompanyEngineC7Config.fssoClientSecret
  override lazy val scope = CompanyEngineC7Config.fssoScope
  override lazy val username = CompanyEngineC7Config.fssoTechuserName
  override lazy val password = CompanyEngineC7Config.fssoTechuserPassword

  override lazy val lockDuration: Long = 5.minutes.toMillis

end CompanyC7Client

object CompanyC7Client extends CompanyC7Client
