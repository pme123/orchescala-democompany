package democompany.orchescala.engine

import democompany.orchescala.engine.CompanyEngineOpConfig.*
import orchescala.engine.c7.{C7LocalClient, C7ProcessEngine, SharedC7ClientManager}
import zio.ZIO

object CompanyEngineOpApp extends EngineApp:

  lazy val client = C7LocalClient(operatonRestUrl)

  override lazy val engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(client)(using companyEngineConfig)
      .provideLayer(SharedC7ClientManager.layer)
end CompanyEngineOpApp
