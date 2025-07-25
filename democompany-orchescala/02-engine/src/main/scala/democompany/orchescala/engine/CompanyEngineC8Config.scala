package democompany.orchescala.engine

/** Using the SAAS version of Camunda 8 (Camunda Champions Access;).
  */
object CompanyEngineC8Config:

  lazy val zeebeGrpc    = sys.env.getOrElse("CAMUNDA8_CLOUD_GRPC_URL", s"NOT_SET")
  lazy val zeebeRest    = sys.env.getOrElse("CAMUNDA8_CLOUD_REST_URL", s"NOT_SET")
  lazy val audience     = sys.env.getOrElse("CAMUNDA8_CLOUD_AUDIENCE", "zeebe.camunda.io")
  lazy val clientId     = sys.env("CAMUNDA8_CLOUD_CLIENTID")
  lazy val clientSecret = sys.env("CAMUNDA8_CLOUD_CLIENTSECRET")
  lazy val oAuthAPI     =
    sys.env.getOrElse("CAMUNDA8_CLOUD_OAUTH_URL", "https://login.cloud.camunda.io/oauth/token")

end CompanyEngineC8Config
