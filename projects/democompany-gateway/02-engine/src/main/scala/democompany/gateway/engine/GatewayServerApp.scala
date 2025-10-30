package democompany.gateway.worker

import democompany.orchescala.engine.*
import orchescala.engine.c7.C7BearerTokenClient
import orchescala.engine.c8.C8BearerTokenClient
import orchescala.engine.gateway.http.GatewayServer
import zio.*

// check if an extra app that gathers all workers should be created ()
object GatewayServerApp extends CompanyGatewayServerApp:
  
  lazy val supportedWorkers = Seq(
    democompany.cards.worker.WorkerApp.theWorkers,
    democompany.services.worker.WorkerApp.theWorkers
  )
  
end GatewayServerApp
