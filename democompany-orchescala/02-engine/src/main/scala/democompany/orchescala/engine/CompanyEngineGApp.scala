package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:
  given EngineConfig = companyEngineConfig

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]]  = Seq(
    SharedC8ClientManager.layer,
    SharedC7ClientManager.layer
  )
  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      c8Engine: ProcessEngine <- CompanyEngineC8App.engineZIO
      c7Engine: ProcessEngine <- CompanyEngineC7App.engineZIO
      given Seq[ProcessEngine] =
        Seq(c8Engine) // , c7Engine) // -> change order to change default engine
    yield GProcessEngine())
      .provideLayer(SharedC7ClientManager.layer ++ SharedC8ClientManager.layer)

end CompanyEngineGApp
