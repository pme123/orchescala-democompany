package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC8App
import orchescala.engine.domain.EngineType

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager - on the local c8run
  * (see CompanyEngineC8Config).
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App:
  def engineType: EngineType = EngineType.C8

  override def config =
    super.config
      .withCockpitUrl(c8OperateUrl)
