package democompany.orchescala.engine

import orchescala.engine.c7.{C7OAuth2Client, C7ProcessEngine, SharedC7ClientManager}
import orchescala.engine.rest.OAuthConfig
import zio.{ZIO, ZLayer}

trait CompanyEngineC7App extends EngineApp, CompanyEngineC7Config, C7OAuth2Client:

  def oAuthConfig: OAuthConfig.ClientCredentials = clientCredentials

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(this)(using companyEngineConfig)
      .provideLayer(SharedC7ClientManager.layer)

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC7ClientManager.layer
  )
end CompanyEngineC7App

object CompanyEngineC7App extends CompanyEngineC7App
