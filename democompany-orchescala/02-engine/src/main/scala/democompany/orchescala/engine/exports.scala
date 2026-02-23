package democompany.orchescala.engine

import _root_.orchescala.engine.{DefaultEngineConfig, ProcessEngine}

lazy val companyEngineConfig = DefaultEngineConfig(
  tenantId = None, // single installation
  impersonateProcessKey = Some("clientKey"),
  identitySigningKey = Some(CompanyEngineC7Config.ssoTechuserPassword)
)
