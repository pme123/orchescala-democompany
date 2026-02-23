package democompany.orchescala.engine

import orchescala.engine.c7.OpProcessEngine

object CompanyEngineOpConfig:

  lazy val operatonRestUrl =
    sys.env.getOrElse("OPERATON_BASE_URL", OpProcessEngine.restUrl)

  lazy val operatonCockpitUrl = sys.env.getOrElse("OPERATON_COCKPIT_URL", OpProcessEngine.cockpitUrl)
  

end CompanyEngineOpConfig
