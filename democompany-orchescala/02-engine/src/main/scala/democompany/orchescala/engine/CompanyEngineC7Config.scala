package democompany.orchescala.engine

import orchescala.engine.rest.OAuthConfig

object CompanyEngineC7Config:

  lazy val ssoBaseUrl = {
    (sys.env.getOrElse("SSO_BASE_URL", s"http://host.lima.internal:8090") + "/auth")
      .replace("/auth/auth", "/auth")
  }

  lazy val camundaRestUrl =
    sys.env.getOrElse("CAMUNDA_BASE_URL", "http://localhost:8080/engine-rest")

  lazy val camundaCockpitUrl = sys.env.getOrElse("CAMUNDA_COCKPIT_URL", ProcessEngine.c7CockpitUrl)
  
  lazy val adminPasswordGrant     =
    OAuthConfig.PasswordGrant(
      ssoRealm = ssoRealm,
      ssoBaseUrl = ssoBaseUrl,
      client_id = ssoClientName,
      client_secret = ssoClientSecret,
      scope = ssoScope,
      username = ssoTechuserName,
      password = ssoTechuserPassword
    )
  lazy val clientCredentials =
    OAuthConfig.ClientCredentials(
      ssoRealm = ssoRealm,
      ssoBaseUrl = ssoBaseUrl,
      client_id = ssoClientName,
      client_secret = ssoClientSecret,
      scope = ssoScope
    )

  lazy val ssoTechuserName = sys.env.getOrElse("SSO_TECHUSER_NAME", "admin")
  lazy val ssoRealm: String = sys.env.getOrElse("SSO_REALM", "0949")

  private lazy val ssoClientName   = sys.env.getOrElse("SSO_CLIENT_NAME", "bpf")
  private lazy val ssoClientSecret =
    sys.env.getOrElse("SSO_CLIENT_SECRET", "6ec0e8ce-eff1-456f-bc2f-907b6fcb5157")
  private lazy val ssoScope        = sys.env.getOrElse("SSO_SCOPE", "bpf fcs")

  private[engine] lazy val ssoTechuserPassword = sys.env.getOrElse("SSO_TECHUSER_PASSWORD", "admin")


end CompanyEngineC7Config
