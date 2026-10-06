// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.customer.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
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
