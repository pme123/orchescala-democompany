package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config.ssoRealm
import democompany.orchescala.engine.{companyEngineConfig, CompanyTokenValidation}

lazy val testPrefix = s"$ssoRealm;"
lazy val corrPrefix = "6300;"
lazy val userPrefix = s"SSO_$ssoRealm--"

val impersonateDiscriminator = ":::"

// workers with roles (requiredRoles) need verified tokens - TOKEN_ISSUER, see CompanyTokenValidation
lazy val companyWorkerConfig =
  DefaultWorkerConfig(engineConfig = companyEngineConfig, tokenValidation = CompanyTokenValidation.fromEnv)