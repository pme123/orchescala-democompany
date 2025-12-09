// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.gateway

import democompany.orchescala.engine.{CompanyEngineC7Config, CompanyEngineC8Config, CompanyEngineGApp}
import orchescala.engine.c7.{C7BearerTokenClient, C7ProcessEngine}
import orchescala.engine.c8.{C8BearerTokenClient, C8ProcessEngine}

// sbt gateway/run
object GatewayServerApp extends GatewayServer, CompanyEngineGApp:
  // You can add single workers, lists of workers or even complete WorkerApps. And a mix of all of them.
  supportedWorkers(
    democompany.services.worker.WorkerApp,
    democompany.cards.worker.WorkerApp
  )

  /** Example C7 client with Bearer token pass-through authentication Note: Do NOT mix with
    * CompanyEngineC7Config as it contains OAuth client credentials which conflicts with bearer
    * token pass-through authentication
    */
  private object BearerPassThroughC7Client extends C7BearerTokenClient:
    override protected def camundaRestUrl: String = CompanyEngineC7Config.camundaRestUrl

  /** Example C8 client with Bearer token pass-through authentication Note: Do NOT mix with
    * CompanyEngineC8Config as it contains OAuth client credentials which conflicts with bearer
    * token pass-through authentication
    */
  private object BearerPassThroughC8Client extends C8BearerTokenClient:
    override protected def zeebeGrpc: String = CompanyEngineC8Config.zeebeGrpc
    override protected def zeebeRest: String = CompanyEngineC8Config.zeebeRest

end GatewayServerApp
