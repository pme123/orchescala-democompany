package democompany.orchescala.engine

import orchescala.engine.gateway.http.GatewayServer
import zio.ZIO

class CompanyEngineGateway(override val engineZIO: ZIO[Any, Nothing, ProcessEngine]) extends GatewayServer:
  override def port: Int = 8888
  
  
end CompanyEngineGateway
