package democompany.orchescala.engine

import orchescala.engine.EnvironmentDetector
import orchescala.engine.domain.EngineType


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

/** The engines of the environment - `COMPANY_ENGINES`, e.g. `Op` or `C7,C8,Op`; the first is the
  * default engine. Without it all three (C7, C8, Op). An engine that is not chosen is not created -
  * an environment without Camunda 8 needs no C8 settings. The gateway and the worker apps use it.
  */
lazy val companyEngineTypes: Seq[EngineType] =
  sys.env
    .get("COMPANY_ENGINES")
    .map(_.split(",").toSeq.map(_.trim).filter(_.nonEmpty).map: name =>
      EngineType.values
        .find(_.toString.equalsIgnoreCase(name))
        .getOrElse(throw IllegalArgumentException(s"COMPANY_ENGINES: unknown engine '$name' - use C7, C8 or Op")))
    .getOrElse(Seq(EngineType.C7, EngineType.C8, EngineType.Op))
