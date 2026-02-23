package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config.ssoRealm
import democompany.orchescala.engine.companyEngineConfig

lazy val testPrefix = s"$ssoRealm;"
lazy val corrPrefix = "6300;"
lazy val userPrefix = s"SSO_$ssoRealm--"

val impersonateDiscriminator = ":::"

lazy val companyWorkerConfig = DefaultWorkerConfig(engineConfig = companyEngineConfig)