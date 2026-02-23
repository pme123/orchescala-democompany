package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineGApp, CompanyEngineOperatonApp, CompanyEngineOperatonConfig, companyEngineConfig}

/** Company-specific Operaton Simulation trait
  *
  * Operaton is compatible with Camunda 7 API, so this follows a similar pattern to CompanyC7Simulation.
  */
trait CompanyOperatonSimulation extends CompanySimulation, CompanyEngineGApp:

  override def engineZIO = CompanyEngineOperatonApp.engineZIO

  override lazy val config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = CompanyEngineOperatonConfig.operatonCockpitUrl
    )

end CompanyOperatonSimulation

