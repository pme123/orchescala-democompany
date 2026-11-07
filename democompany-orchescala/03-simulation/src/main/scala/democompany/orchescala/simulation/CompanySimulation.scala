package democompany.orchescala.simulation

import democompany.orchescala.engine.companyEngineConfig

/** The base of the simulations of the company: the engine config of the company (`WORKER_APP_URL`
  * for the init workers, e.g. the gateway of the demo stack). The simulations and the engine traits
  * (CompanyC8Simulation) build on this `config` - they do not replace it.
  */
trait CompanySimulation extends SimulationRunner:

  override def config: SimulationConfig =
    DefaultSimulationConfig(engineConfig = companyEngineConfig)

end CompanySimulation
