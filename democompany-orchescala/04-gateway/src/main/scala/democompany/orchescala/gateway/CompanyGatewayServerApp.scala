package democompany.orchescala.gateway

import democompany.orchescala.engine.{
  CompanyEngineC7Config,
  CompanyEngineC8Config,
  CompanyEngineGApp
}
import orchescala.engine.c7.{C7ProcessEngine, SharedC7ClientManager}
import orchescala.engine.c8.{C8ProcessEngine, SharedC8ClientManager}
import orchescala.engine.gateway.GProcessEngine
import orchescala.engine.c7.C7BearerTokenClient
import orchescala.engine.c8.C8BearerTokenClient
import orchescala.engine.domain.EngineError
import orchescala.gateway.GatewayServer
import zio.*

// checking if use extra service app to gather all workers - see democompany-gateway
trait CompanyGatewayServerApp extends GatewayServer, CompanyEngineGApp, ZIOAppDefault:

  override def port: Int = 8888

  // Override to disable authentication - always accept any token (or no token)
  override protected lazy val validateToken: String => IO[EngineError, String] =
    token => ZIO.succeed(token) // Always succeed, no validation
  /*
  override lazy val c7Engine = C7ProcessEngine.withClient(BearerPassThroughC7Client)
    .provideLayer(SharedC7ClientManager.layer)
  override lazy val c8Engine = C8ProcessEngine.withClient(BearerPassThroughC8Client)
    .provideLayer(SharedC8ClientManager.layer)

  /** Example C7 client with Bearer token pass-through authentication
    * Note: Do NOT mix with CompanyEngineC7Config as it contains OAuth client credentials
    * which conflicts with bearer token pass-through authentication
    */
  private object BearerPassThroughC7Client extends C7BearerTokenClient:
    override protected def camundaRestUrl: String = CompanyEngineC7Config.camundaRestUrl

  /** Example C8 client with Bearer token pass-through authentication
    * Note: Do NOT mix with CompanyEngineC8Config as it contains OAuth client credentials
    * which conflicts with bearer token pass-through authentication
    */
  private object BearerPassThroughC8Client extends C8BearerTokenClient:
    override protected def zeebeGrpc: String = CompanyEngineC8Config.zeebeGrpc
    override protected def zeebeRest: String = CompanyEngineC8Config.zeebeRest
*/
  override def run: ZIO[Any, Any, Any] = start()
end CompanyGatewayServerApp
