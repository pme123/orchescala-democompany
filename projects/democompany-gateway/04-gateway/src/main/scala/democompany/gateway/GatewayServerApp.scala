// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.gateway

import orchescala.engine.DefaultEngineConfig

// sbt gateway/run
object GatewayServerApp extends GatewayServer, CompanyEngineGApp:
  lazy val config = DefaultGatewayConfig(
    engineConfig = DefaultEngineConfig()
  )
end GatewayServerApp