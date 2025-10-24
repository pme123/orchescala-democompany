package democompany.orchescala.engine

import orchescala.engine.c7.{C7ProcessEngine, SharedC7ClientManager}
import orchescala.engine.c8.SharedC8ClientManager
import zio.{ZIO, ZLayer}

trait CompanyEngineC7App extends EngineApp, CompanyEngineC7Config, CompanyEngineC7Client:
  given EngineConfig = companyEngineConfig

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC7ClientManager.layer
  )

  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(this)
      .provideLayer(SharedC7ClientManager.layer)
      
end CompanyEngineC7App 

object CompanyEngineC7App extends CompanyEngineC7App