// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.customer.worker

import orchescala.worker.op.OpWorkerRegistry

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  // engineType Op (PROJECT.conf): only Operaton - the company app has C7 and C8 as well
  override lazy val engineContext    = CompanyEngineOpContext(CompanyRestApiOpClient())
  override lazy val workerRegistries = Seq(OpWorkerRegistry(CompanyOpClient))
  // the port of the app (`/worker`, `/ui`) - WORKER_APP_PORT, default 5555
  override def port: Int = sys.env.get("WORKER_APP_PORT").map(_.toInt).getOrElse(super.port)

  workers(
    appointmentsWorkers,
    bookAppointmentWorkers,
    updateAvailabilityWorkers,
    //TODO add workers here
  )
  dependencies(
    
  )

  // the services of the area appointments - without process, over the gateway
  private lazy val appointmentsWorkers =
    import democompany.customer.worker.appointments.*
    Seq(
      FreeSlotsWorker(),
      ReserveSlotWorker(),
    )
  end appointmentsWorkers

  private lazy val bookAppointmentWorkers =
    import democompany.customer.worker.bookAppointment.v1.*
    Seq(
      BookAppointmentWorker(),
      AssignCustomerWorker(),
      BookReservationWorker(),
      SendInvitationWorker(),
      ReleaseReservationWorker(),
      SendMailWorker(),
    )
  end bookAppointmentWorkers

  private lazy val updateAvailabilityWorkers =
    import democompany.customer.worker.updateAvailability.v1.*
    Seq(
      UpdateAvailabilityWorker(),
      LoadAdvisorsWorker(),
      ReadCalendarWorker(),
      SaveCalendarSnapshotWorker(),
    )
  end updateAvailabilityWorkers

end WorkerApp
