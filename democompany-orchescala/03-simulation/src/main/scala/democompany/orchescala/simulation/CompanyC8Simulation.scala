package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC8App
import orchescala.engine.domain.EngineType

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App:
  def engineType: EngineType = EngineType.C8 // Operaton uses C7-compatible API

