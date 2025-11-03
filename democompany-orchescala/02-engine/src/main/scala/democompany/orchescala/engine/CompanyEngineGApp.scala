package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:
  given EngineConfig = companyEngineConfig

  def c7Engine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineC7App.engineZIO
  def c8Engine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineC8App.engineZIO

  // Override this to provide the ZIO layers required by the simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]]  = Seq(
    SharedC8ClientManager.layer,
    SharedC7ClientManager.layer
  )
  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      c8Engine: ProcessEngine <- c8Engine
      c7Engine: ProcessEngine <- c7Engine
      given Seq[ProcessEngine] =
        Seq(c8Engine) // , c7Engine) // -> change order to change default engine
    yield GProcessEngine())
    // .provideLayer(requiredLayers.reduce(_ ++ _))

end CompanyEngineGApp
