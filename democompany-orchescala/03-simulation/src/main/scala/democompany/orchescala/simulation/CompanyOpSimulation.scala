package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineGApp, CompanyEngineOpApp, CompanyEngineOpConfig, companyEngineConfig}
import orchescala.engine.EngineApp
import orchescala.engine.domain.EngineType

/** Company-specific Operaton Simulation trait
  *
  * Operaton is compatible with Camunda 7 API, so this follows a similar pattern to CompanyC7Simulation.
  */
trait CompanyOpSimulation extends CompanySimulation, CompanyEngineGApp:
  def engineType: EngineType = EngineType.Op // Operaton uses C7-compatible API


