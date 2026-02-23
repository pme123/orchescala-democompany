package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineGApp, CompanyEngineOpConfig, companyEngineConfig}


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyOpSimulation extends CompanySimulation, CompanyEngineGApp:
  
  override lazy val config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = CompanyEngineOpConfig.operatonCockpitUrl
    )
