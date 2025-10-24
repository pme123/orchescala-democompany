package democompany.orchescala.simulation

import democompany.orchescala.engine.{
  CompanyEngineC8App,
  CompanyEngineC8Client,
  CompanyEngineC8Config
}
import orchescala.engine.ProcessEngine
import orchescala.engine.c8.*
import zio.{ZIO, ZLayer}

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App:

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = zeebeOperateUrl
    )

end CompanyC8Simulation
