package democompany.orchescala.gateway

import democompany.orchescala.engine.{CompanyEngineGApp, companyEngineConfig}
import democompany.orchescala.worker.companyWorkerConfig

// sbt gateway/run
object GatewayServerApp extends GatewayServer, CompanyEngineGApp:

  // CompanyEngineGApp provides the engine - C7, C8 and Operaton behind one Gateway.
  override lazy val config: GatewayConfig =
    DefaultGatewayConfig(
      engineConfig = companyEngineConfig,
      workerConfig = companyWorkerConfig,
      tokenValidation = CompanyGatewayConfig.tokenValidation,
      publicAccess = CompanyGatewayConfig.publicAccess,
      // GATEWAY_PORT - e.g. next to another gateway on 8888
      gatewayPort = sys.env.get("GATEWAY_PORT").filter(_.nonEmpty).map(_.toInt).getOrElse(8887)
    )

end GatewayServerApp
