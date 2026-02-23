package democompany.orchescala.engine

import democompany.orchescala.engine.CompanyEngineOperatonConfig.*
import orchescala.engine.c7.{C7OAuth2Client, C7ProcessEngine, SharedC7ClientManager}
import zio.{ZIO, ZLayer}

/** Operaton engine app using C7-compatible API.
  * Operaton is a fork of Camunda 7 with full REST API compatibility.
  */
object CompanyEngineOperatonApp extends EngineApp:

  lazy val client = C7OAuth2Client(operatonRestUrl, clientCredentials)

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(client)(using companyEngineConfig)
      .provideLayer(SharedC7ClientManager.layer)
end CompanyEngineOperatonApp

