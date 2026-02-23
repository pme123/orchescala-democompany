package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC7Config, CompanyEngineGApp, companyEngineConfig}


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyC7Simulation extends CompanySimulation, CompanyEngineGApp:

  override lazy val config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = CompanyEngineC7Config.camundaCockpitUrl
    )
