// This file was created with `./helper.scala update` - to reset delete it and run the command.
package democompany.customer.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    bookAppointmentWorkers,
    updateAvailabilityWorkers,
    //TODO add workers here
  )
  dependencies(
    
  )

  private lazy val bookAppointmentWorkers =
    import democompany.customer.worker.bookAppointment.v1.*
    Seq(
      BookAppointmentWorker(),
      AssignCustomerWorker(),
      BookReservationWorker(),
      SendInvitationWorker(),
      ReleaseReservationWorker(),
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
