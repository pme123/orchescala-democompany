package democompany.customer
package simulation

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
import democompany.customer.domain.bookAppointment.v1.schema.{Channel, CustomerStatus, Topic}
import democompany.customer.domain.bookAppointment.v1.{ConfirmAppointmentUT, EmailVerifiedME}

// ./helper.scala deploy BookAppointmentSimulation
// simulation/test
// simulation/testOnly *BookAppointmentSimulation
// against the demo stack: orch-platform/demo/simulate.sh '*BookAppointmentSimulation'
class BookAppointmentSimulation extends CompanyOpSimulation:

  simulate(
    // a mortgage: the advisor confirms
    scenario(`BookAppointment`)(
      `EmailVerified`,
      `ConfirmAppointment accepted`
    ),
    // advice for a customer: no confirmation - booked after the opt-in
    scenario(`BookAppointment minimal`)(
      `EmailVerified`
    ),
    scenario(`BookAppointment declined`)(
      `EmailVerified`,
      `ConfirmAppointment declined`
    ),
    // a start from the homepage without a reservation - no process instance
    badScenario(`BookAppointment without reservation`, "No valid reservation")
  )

  override def config =
    super.config
      .withMaxCount(30) // the worker app polls its tasks - a step can take some seconds
      //.withLogLevel(LogLevel.DEBUG)

  // the reservations come first, like on the page «Termin buchen» - one slot each
  private lazy val reservations =
    DemoStack.reserve(Seq((Topic.mortgage, Channel.branch), (Topic.advice, Channel.branch), (Topic.mortgage, Channel.video)))

  private lazy val `BookAppointment` =
    booking(reservations(0))
      .mockServices
      .mockWorkers(workers*)

  private lazy val `BookAppointment minimal` =
    booking(reservations(1))
      .mockServices
      .mockWorkers(workers*)

  private lazy val `BookAppointment declined` =
    booking(reservations(2), booked = false)
      .mockServices
      .mockWorkers(workers*)

  private lazy val `BookAppointment without reservation` =
    example

  private lazy val `EmailVerified`               = EmailVerifiedME.example
  private lazy val `ConfirmAppointment accepted` = confirm(reservations(0))
  private lazy val `ConfirmAppointment declined` =
    confirm(reservations(2)).withOut(_.copy(accepted = false, declineReason = Some("Bitte einen Termin am Nachmittag")))

  /** The task of the advisor - with the reserved slot. */
  private def confirm(r: DemoStack.Reserved) =
    ConfirmAppointmentUT.example.withIn(
      _.copy(appointment = r.appointment, contact = r.contact, customerStatus = CustomerStatus.customer, remark = In.example.remark)
    )

  /** The process of a reservation - with what it ends. */
  private def booking(r: DemoStack.Reserved, booked: Boolean = true) =
    example
      .withIn(_.copy(reservationId = r.reservationId, token = r.token, appointment = r.appointment, contact = r.contact))
      .withOut(_.copy(reservationId = r.reservationId, booked = booked, customerStatus = Some(CustomerStatus.customer)))

  private lazy val workers = Seq()

end BookAppointmentSimulation
