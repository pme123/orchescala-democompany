package democompany.orchescala.engine


lazy val companyEngineConfig = DefaultEngineConfig(
  tenantId = None, // single installation
  impersonateProcessKey = Some("clientKey"),
  identitySigningKey = Some(CompanyEngineC7Config.ssoTechuserPassword),
  validateInput = true
)
