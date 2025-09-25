package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC7Config, CompanyEngineC8Config}
import orchescala.engine.ProcessEngine
import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.{C8ProcessEngine, C8SaasClient, SharedC8ClientManager}
import orchescala.engine.domain.EngineType
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyGSimulation extends CompanySimulation, C8SaasClient, CompanyEngineC7Config,
      CompanyEngineC8Config:

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]]  = Seq(
    SharedC8ClientManager.layer,
    SharedC7ClientManager.layer
  )
  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      c8Engine: ProcessEngine <- C8ProcessEngine.withClient(this)
      c7Engine: ProcessEngine <- CompanyC7Simulation.engineZIO
      given Seq[ProcessEngine] = Seq(c8Engine,c7Engine)
    yield GProcessEngine())
      .provideLayer(SharedC8ClientManager.layer)
      .provideLayer(SharedC7ClientManager.layer)

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = Map(
        EngineType.C7 -> camundaCockpitUrl,
        EngineType.C8 -> zeebeOperateUrl
      )
    )
end CompanyGSimulation
