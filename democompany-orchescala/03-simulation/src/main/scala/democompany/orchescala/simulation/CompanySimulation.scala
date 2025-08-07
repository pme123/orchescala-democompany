package democompany.orchescala.simulation

import orchescala.engine.EngineConfig

trait CompanySimulation extends SimulationRunner:
  given EngineConfig = EngineConfig(tenantId = config.tenantId)
