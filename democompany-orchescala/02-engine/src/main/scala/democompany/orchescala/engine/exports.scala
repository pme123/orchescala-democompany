package democompany.orchescala.engine

lazy val companyEngineConfig = EngineConfig(
  tenantId = None, // single installation
  impersonateProcessKey = Some("clientKey"),
  identitySigningKey = Some(CompanyEngineC7Config.ssoTechuserPassword)
)
