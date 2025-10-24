package democompany.orchescala.simulation

import orchescala.engine.EngineConfig

trait CompanySimulation extends SimulationRunner:
  given EngineConfig = companyEngineConfig
