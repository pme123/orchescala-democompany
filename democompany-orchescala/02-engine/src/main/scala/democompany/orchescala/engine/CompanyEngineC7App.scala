package democompany.orchescala.engine

import democompany.orchescala.engine.CompanyEngineC7Config.*
import orchescala.engine.c7.{C7OAuth2Client, C7ProcessEngine, SharedC7ClientManager}
import zio.{ZIO, ZLayer}

object CompanyEngineC7App extends EngineApp:

  lazy val client = C7OAuth2Client(camundaRestUrl, clientCredentials)

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(client)(using companyEngineConfig)
      .provideLayer(SharedC7ClientManager.layer)
end CompanyEngineC7App
