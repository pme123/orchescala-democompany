package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config.fssoRealm

lazy val testPrefix = s"$fssoRealm;"
lazy val corrPrefix = "6300;"
lazy val userPrefix = s"FSSO_$fssoRealm--"

val impersonateDiscriminator = ":::"
