package democompany.cards.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    orderCreditcardWorkers
  )
  dependencies(
  )

  private lazy val orderCreditcardWorkers =
    import democompany.cards.worker.orderCreditcard.v1.*
    Seq(
      new OrderCreditcardWorker()
    )
  end orderCreditcardWorkers
end WorkerApp
