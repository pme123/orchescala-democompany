package democompany.orchescala.engine

import orchescala.engine.c7.C7Client

/** Company-specific Operaton engine client.
  * Operaton is compatible with Camunda 7 API, so we use C7Client.
  */
trait CompanyEngineOpClient extends C7Client:

  lazy val camundaRestUrl: String = CompanyEngineOpConfig.operatonRestUrl

end CompanyEngineOpClient

