package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineGApp
import orchescala.engine.domain.EngineType


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyC7Simulation extends CompanySimulation, CompanyEngineGApp:
  def engineType: EngineType = EngineType.C7

