package democompany.orchescala.engine

import orchescala.engine.rest.OAuthConfig

/** Configuration for Operaton BPM engine.
  * Operaton is compatible with Camunda 7 API.
  */
object CompanyEngineOperatonConfig:

  lazy val ssoBaseUrl = {
    (sys.env.getOrElse("SSO_BASE_URL", s"http://host.lima.internal:8090") + "/auth")
      .replace("/auth/auth", "/auth")
  }

  lazy val operatonRestUrl =
    sys.env.getOrElse("OPERATON_BASE_URL", "http://localhost:8080/engine-rest")

  lazy val operatonCockpitUrl = sys.env.getOrElse("OPERATON_COCKPIT_URL", "http://localhost:8080/camunda")
  
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


end CompanyEngineOperatonConfig

