package democompany.customer
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "democompany-customer"

  lazy val projectDescr =
    "Kunden-PoCs der democompany - zuerst die Terminbuchung (appointments)."

  val version = "0.1.0-SNAPSHOT"

  document(
    appointmentsApi,
    bookAppointmentApi,
    updateAvailabilityApi,
    //myProcessApi,
    //..
  )

  /* example:

  private lazy val myProcessApi =
    import myProcess.v1.*
    api(MyProcess.example)(
      // userTasks / workers etc.
    )
  */

  // the services of the area appointments - without process
  private lazy val appointmentsApi =
    import democompany.customer.domain.appointments.*
    group("Termine")(
      FreeSlots.example,
      ReserveSlot.example,
      AppointmentToConfirm.example,
    )
  end appointmentsApi

  private lazy val bookAppointmentApi =
    import democompany.customer.domain.bookAppointment.v1.*
    api(BookAppointment.example)(
      EmailVerifiedME.example,
      AssignCustomer.example,
      AppointmentRulesDmn.example,
      ConfirmAppointmentUT.example,
      BookReservation.example,
      SendInvitation.example,
      ReleaseReservation.example,
      SendMail.example,
    )
  end bookAppointmentApi

  private lazy val updateAvailabilityApi =
    import democompany.customer.domain.updateAvailability.v1.*
    api(UpdateAvailability.example)(
      LoadAdvisors.example,
      ReadCalendar.example,
      SaveCalendarSnapshot.example,
    )
  end updateAvailabilityApi

end ApiProjectCreator
