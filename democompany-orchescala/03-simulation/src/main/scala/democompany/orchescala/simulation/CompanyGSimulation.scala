package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC7Config, CompanyEngineC8Config, CompanyEngineGApp, CompanyEngineGClient, CompanyEngineGConfig}
import orchescala.engine.ProcessEngine
import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.{C8ProcessEngine, C8SaasClient, SharedC8ClientManager}
import orchescala.engine.domain.EngineType
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyGSimulation extends CompanySimulation, CompanyEngineGApp:

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = Map(
        EngineType.C7 -> CompanyEngineC7Config.camundaCockpitUrl,
        EngineType.C8 -> CompanyEngineC8Config.zeebeOperateUrl
      )
    )
end CompanyGSimulation
