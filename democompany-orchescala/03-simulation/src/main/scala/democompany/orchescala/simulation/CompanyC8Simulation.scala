package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC8App, CompanyEngineC8Config, CompanyEngineOpConfig, companyEngineConfig}

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App:

  override lazy val config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = CompanyEngineC8Config.zeebeOperateUrl
    )

