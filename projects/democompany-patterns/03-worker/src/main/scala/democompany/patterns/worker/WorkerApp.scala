// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.patterns.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    initProcessWorkers,
    identityWorkers,
    propagateMocksWorkers
  )
  dependencies(
  )

  // WORKER_APP_PORT - if 5555 is taken by another worker app
  override def port: Int =
    sys.env.get("WORKER_APP_PORT").map(_.toInt).getOrElse(super.port)

  private lazy val initProcessWorkers =
    import democompany.patterns.worker.initProcess.v1.*
    Seq(
      new InitProcessWorker()
    )
  end initProcessWorkers

  private lazy val identityWorkers =
    import democompany.patterns.worker.identity.v1.*
    Seq(
      new IdentityWorker(),
      new CheckIdentityWorker()
    )
  end identityWorkers

  private lazy val propagateMocksWorkers =
    import democompany.patterns.worker.propagateMocks.v1.*
    Seq(
      new PropagateMocksWorker(),
      new PropagateMocksSubWorker(),
      new CheckCustomerWorker()
    )
  end propagateMocksWorkers
end WorkerApp
