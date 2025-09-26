package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC8Config
import orchescala.engine.ProcessEngine
import orchescala.engine.c8.*
import zio.{ZIO, ZLayer}

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8Config,
      C8SaasClient:

  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C8ProcessEngine.withClient(this)
      .provideLayer(SharedC8ClientManager.layer)

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC8ClientManager.layer
  )

  override lazy val config: SimulationConfig =
    SimulationConfig(
      cockpitUrl = zeebeOperateUrl
    )

end CompanyC8Simulation
