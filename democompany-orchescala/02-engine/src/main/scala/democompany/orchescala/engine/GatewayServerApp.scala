package democompany.orchescala.engine

import orchescala.engine.c7.C7BearerTokenClient
import orchescala.engine.c8.C8BearerTokenClient
import orchescala.engine.gateway.http.GatewayServer
import zio.*

object GatewayServerApp extends GatewayServer, CompanyEngineGApp, ZIOAppDefault:

  /** Example C7 client with Bearer token pass-through authentication */
  object BearerPassThroughC7Client extends C7BearerTokenClient, CompanyEngineC7Config

  object BearerPassThroughC8Client extends C8BearerTokenClient, CompanyEngineC8Config

  override def port: Int = 8888

  override def run: ZIO[Any, Any, Any] = start()
end GatewayServerApp
