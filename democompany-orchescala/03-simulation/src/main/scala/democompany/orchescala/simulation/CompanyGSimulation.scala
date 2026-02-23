package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC7Config, CompanyEngineC8Config, CompanyEngineGApp, CompanyEngineOpConfig, companyEngineConfig}
import orchescala.engine.domain.EngineType

trait CompanyGSimulation extends CompanySimulation, CompanyEngineGApp:

  override lazy val config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = Map(
        EngineType.C7 -> CompanyEngineC7Config.camundaCockpitUrl,
        EngineType.Op -> CompanyEngineOpConfig.operatonCockpitUrl,
        EngineType.C8 -> CompanyEngineC8Config.zeebeOperateUrl
      )
    )
end CompanyGSimulation
