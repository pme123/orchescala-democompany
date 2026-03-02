package democompany.orchescala.engine

import orchescala.engine.op.{OpLocalClient, OpOAuth2Client, OpProcessEngine, SharedOpClientManager}
import orchescala.engine.rest.OAuthConfig
import zio.{ZIO, ZLayer}

trait CompanyEngineOpApp extends EngineApp, CompanyEngineOpConfig, OpLocalClient:

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    OpProcessEngine.withClient(this)(using companyEngineConfig)
      .provideLayer(SharedOpClientManager.layer)

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedOpClientManager.layer
  )
end CompanyEngineOpApp

object CompanyEngineOpApp extends CompanyEngineOpApp
