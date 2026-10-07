package democompany.orchescala.engine

import orchescala.engine.EnvironmentDetector


lazy val companyEngineConfig = DefaultEngineConfig(
  tenantId = None, // single installation
  impersonateProcessKey = Some("clientKey"),
  identitySigningKey = Some(CompanyEngineC7Config.ssoTechuserPassword),
  validateInput = true,
  // WORKER_APP_URL: one address for all worker apps - e.g. the gateway (it forwards /worker/{topic})
  // for a simulation on the host; else per project (`http://{project}:5555`, locally localhost)
  workerAppUrl = topicName =>
    sys.env.get("WORKER_APP_URL").filter(_.nonEmpty)
      .orElse(DefaultEngineConfig.defaultWorkerAppUrl(topicName, EnvironmentDetector.isLocalhost))
)
