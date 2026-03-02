package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC7App
import orchescala.engine.domain.EngineType


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyC7Simulation extends CompanySimulation, CompanyEngineC7App:
  def engineType: EngineType = EngineType.C7

