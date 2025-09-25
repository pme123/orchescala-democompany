package democompany.orchescala
package engine

/** Add here company specific stuff, to configure the Engine.
  */
trait CompanyEngineC7Config:

  lazy val fssoClientName   = sys.env.getOrElse("FSSO_CLIENT_NAME", "myClient")
  lazy val fssoClientSecret =
    sys.env.getOrElse("FSSO_CLIENT_SECRET", "mySecret")
  lazy val fssoScope        = sys.env.getOrElse("FSSO_SCOPE", "myScope")

  lazy val fssoTechuserName     = sys.env.getOrElse("FSSO_TECHUSER_NAME", "admin")
  lazy val fssoTechuserPassword = sys.env.getOrElse("FSSO_TECHUSER_PASSWORD", "admin")

  lazy val fssoRealm: String = sys.env.getOrElse("FSSO_REALM", "MY_REALM")
  lazy val fssoBaseUrl       = sys.env.getOrElse("FSSO_BASE_URL", s"http://host.lima.internal:8090")
  lazy val camundaRestUrl    = sys.env.getOrElse("CAMUNDA_BASE_URL", ProcessEngine.c7Endpoint)
  lazy val camundaCockpitUrl = sys.env.getOrElse("CAMUNDA_COCKPIT_URL", ProcessEngine.c7CockpitUrl)

  lazy val client_id     = fssoClientName
  lazy val client_secret = fssoClientSecret
  lazy val scope         = fssoScope
  lazy val username      = fssoTechuserName
  lazy val password      = fssoTechuserPassword

end CompanyEngineC7Config
object CompanyEngineC7Config extends CompanyEngineC7Config
