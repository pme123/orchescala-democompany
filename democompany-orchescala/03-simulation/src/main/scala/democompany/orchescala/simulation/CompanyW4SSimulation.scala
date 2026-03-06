package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineW4SApp
import orchescala.engine.domain.EngineType

/** Company-specific W4S (Workflows4s) Simulation trait
  *
  * W4S is an in-process engine — no external connections needed.
  */
trait CompanyW4SSimulation extends CompanySimulation, CompanyEngineW4SApp:
  def engineType: EngineType = EngineType.W4S

