// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
package democompany.gateway

// sbt gateway/run
object GatewayServerApp extends CompanyGatewayServerApp:
  // You can add single workers, lists of workers or even complete WorkerApps. And a mix of all of them.
  supportedWorkers(
    democompany.services.worker.WorkerApp,
    democompany.cards.worker.WorkerApp
  )
end GatewayServerApp