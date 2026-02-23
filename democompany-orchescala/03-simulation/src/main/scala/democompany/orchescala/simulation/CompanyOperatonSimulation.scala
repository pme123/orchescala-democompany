package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineOperatonApp, CompanyEngineOperatonConfig}

/** Company-specific Operaton Simulation trait
  * 
  * Operaton is compatible with Camunda 7 API, so this follows a similar pattern to CompanyC7Simulation.
  */
trait CompanyOperatonSimulation extends CompanySimulation, CompanyEngineOperatonApp:

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = operatonCockpitUrl
    )

end CompanyOperatonSimulation

