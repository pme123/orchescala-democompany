package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.op.SharedOpClientManager
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:
  
  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] =
    CompanyEngineC8App.requiredLayers ++
      CompanyEngineC7App.requiredLayers ++
      CompanyEngineOpApp.requiredLayers

  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      c7Engine                <- c7Engine
      c8Engine                <- c8Engine
      opEngine                <- opEngine
      given Seq[ProcessEngine] =
        Seq(c7Engine, c8Engine, opEngine) // -> change order to change default engine
    yield GProcessEngine()(using companyEngineConfig))
      .provideLayer(
        /*SharedC7ClientManager.layer ++ SharedC8ClientManager.layer ++ */ SharedOpClientManager.layer
      )

  private lazy val c7Engine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineC7App.engineZIO
  private lazy val c8Engine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineC8App.engineZIO
  private lazy val opEngine: ZIO[Any, Nothing, ProcessEngine] = CompanyEngineOpApp.engineZIO

end CompanyEngineGApp
