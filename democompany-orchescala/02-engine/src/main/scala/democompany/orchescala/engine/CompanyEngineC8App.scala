package democompany.orchescala.engine

import orchescala.engine.c8.{C8ProcessEngine, SharedC8ClientManager}
import zio.{ZIO, ZLayer}

trait CompanyEngineC8App extends EngineApp, CompanyEngineC8Config, CompanyEngineC8Client:
  given EngineConfig = companyEngineConfig

  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C8ProcessEngine.withClient(this)
      .provideLayer(SharedC8ClientManager.layer)

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC8ClientManager.layer
  )

end CompanyEngineC8App 

object CompanyEngineC8App extends CompanyEngineC8App
