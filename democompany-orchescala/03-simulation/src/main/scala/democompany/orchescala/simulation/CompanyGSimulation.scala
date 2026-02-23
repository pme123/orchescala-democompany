package democompany.orchescala.simulation

import democompany.orchescala.engine.{
  CompanyEngineC7Config,
  CompanyEngineC8Config,
  CompanyEngineOperatonConfig,
  CompanyEngineGApp
}
import orchescala.engine.domain.EngineType

trait CompanyGSimulation extends CompanySimulation, CompanyEngineGApp:

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = Map(
        EngineType.C7 -> CompanyEngineC7Config.camundaCockpitUrl,
        EngineType.C8 -> CompanyEngineC8Config.zeebeOperateUrl,
        EngineType.Op -> CompanyEngineOperatonConfig.operatonCockpitUrl
      )
    )
end CompanyGSimulation
