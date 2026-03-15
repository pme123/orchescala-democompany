package democompany.orchescala.engine

import orchescala.engine.w4s.{SharedW4SClientManager, W4SEngineApp, W4SProcessEngine}
import zio.{ZIO, ZLayer}

trait CompanyEngineW4SApp extends W4SEngineApp:
  override def w4sEngineConfig: EngineConfig = companyEngineConfig

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] = ???
  //  W4SProcessEngine(using companyEngineConfig)
  //    .provideLayer(SharedW4SClientManager.layer)
  
  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedW4SClientManager.layer
  )
object CompanyEngineW4SApp extends CompanyEngineW4SApp

