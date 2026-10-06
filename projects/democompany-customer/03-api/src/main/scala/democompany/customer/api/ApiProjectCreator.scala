package democompany.customer
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "democompany-customer"

  lazy val projectDescr =
    "TODO Your Project description."

  val version = "0.1.0-SNAPSHOT"

  document(
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
