package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC8App, CompanyEngineC8Client, CompanyEngineC8Config, companyEngineConfig}
import orchescala.engine.ProcessEngine
import orchescala.engine.c8.*
import zio.{ZIO, ZLayer}

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App
