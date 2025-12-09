package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config.ssoRealm

lazy val testPrefix = s"$ssoRealm;"
lazy val corrPrefix = "6300;"
lazy val userPrefix = s"SSO_$ssoRealm--"

val impersonateDiscriminator = ":::"
