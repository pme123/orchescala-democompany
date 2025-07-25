package democompany.orchescala.engine

/** Using the SAAS version of Camunda 8 (Camunda Champions Access;).
  */
object CompanyEngineC8Config:

  lazy val camundaCuttingEdgeCluster = "3bddf918-441e-48d0-99c6-e8c808030085"
  lazy val camundaDemoCluster        = "dbd4cad1-5621-4d66-b14e-71c92456939a"
  //
  lazy val camundaCluster            = camundaCuttingEdgeCluster

  lazy val zeebeGrpc = sys.env.getOrElse(
    "CAMUNDA8_CLOUD_GRPC_URL",
    s"https://$camundaCluster.bru-2.zeebe.camunda.io:443"
  )
  lazy val zeebeRest = sys.env.getOrElse(
    "CAMUNDA8_CLOUD_REST_URL",
    s"https://bru-2.zeebe.camunda.io:443/$camundaCluster/v2"
  )

  lazy val audience     = sys.env.getOrElse("CAMUNDA8_CLOUD_AUDIENCE", "zeebe.camunda.io")
  lazy val clientId     = sys.env("CAMUNDA8_CLOUD_CLIENTID")
  lazy val clientSecret = sys.env("CAMUNDA8_CLOUD_CLIENTSECRET")
  lazy val oAuthAPI     = sys.env.getOrElse("CAMUNDA8_CLOUD_OAUTH_URL", "https://login.cloud.camunda.io/oauth/token")

end CompanyEngineC8Config
