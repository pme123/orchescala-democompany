package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.op.SharedOpClientManager
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:

  def c7Engine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineC7App.engineZIO

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]]  = Seq(
    SharedC8ClientManager.layer,
    SharedC7ClientManager.layer,
    SharedOpClientManager.layer
  )
  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      c7Engine: ProcessEngine <- c7Engine
      given Seq[ProcessEngine] =
        Seq(c7Engine) // -> change order to change default engine
    yield GProcessEngine()(using companyEngineConfig))
      .provideLayer(SharedC7ClientManager.layer)

end CompanyEngineGApp
