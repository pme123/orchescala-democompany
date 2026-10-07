package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineOpApp, CompanyEngineOpConfig, companyEngineConfig}
import orchescala.engine.domain.EngineType

/** Company-specific Operaton Simulation trait
  *
  * Operaton is compatible with Camunda 7 API, so this follows a similar pattern to CompanyC7Simulation.
  * With the engine config of the company (e.g. `WORKER_APP_URL` for the init workers) and the
  * technical user as login for the worker apps (they verify Bearer tokens).
  */
trait CompanyOpSimulation extends CompanySimulation, CompanyEngineOpApp:
  def engineType: EngineType = EngineType.Op // Operaton uses C7-compatible API

  override def config: SimulationConfig =
    DefaultSimulationConfig(
      engineConfig = companyEngineConfig,
      cockpitUrl = CompanyEngineOpConfig.operatonCockpitUrl,
      workerAppAuth = Some(CompanyEngineOpConfig.adminPasswordGrant)
    )
end CompanyOpSimulation
