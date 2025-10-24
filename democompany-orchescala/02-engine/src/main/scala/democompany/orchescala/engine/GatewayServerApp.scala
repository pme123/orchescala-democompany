package democompany.orchescala.engine

import orchescala.engine.c7.C7Client
import orchescala.engine.c8.C8Client
import zio.*

/** Example application that starts the Engine Gateway HTTP server.
  *
  * This demonstrates how to configure and start the Gateway with both C7 and C8 engines.
  *
  * To run this application:
  *   1. Make sure you have Camunda 7 and/or Camunda 8 running
  *   2. Adjust the client configurations below to match your setup
  *   3. Run the application
  *   4. Access the API at http://localhost:8080
  *
  */
object GatewayServerApp extends ZIOAppDefault, CompanyEngineC7App:

  def run                                                  =
    CompanyEngineGateway(engineZIO).start()
      .forever

end GatewayServerApp
