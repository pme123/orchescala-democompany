package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Topic}

//sbt worker/testOnly *BookAppointmentWorkerTest
class BookAppointmentWorkerTest extends munit.FunSuite:

  lazy val worker = BookAppointmentWorker()

  private val in = In.example.copy(
    reservationId = "anna.berater-2026-10-20T09:00",
    appointment = Appointment.example.copy(
      start = LocalDateTime.of(2026, 10, 20, 9, 0),
      end = LocalDateTime.of(2026, 10, 20, 10, 30),
      topic = Topic.mortgage,
      channel = Channel.branch
    )
  )

  test("customInit - the variables for the mails and the appointment rules"):
    val init = worker.customInit(in)
    assertEquals(init.email, in.contact.email)
    assertEquals(init.customerName, s"${in.contact.firstName} ${in.contact.lastName}")
    assertEquals(init.advisorEmail, "anna.berater@democompany.ch")
    assertEquals((init.topic, init.channel), ("mortgage", "branch"))
    assertEquals(init.appointmentText, "Di 20.10.2026, 09:00-10:30, Hypothek, Filiale")

  test("customInit - the links carry the reservation"):
    val init = worker.customInit(in)
    assert(init.verificationLink.endsWith("/appointments/verified?reservation=anna.berater-2026-10-20T09:00"))
    assert(init.confirmLink.endsWith("/appointments/confirm?reservation=anna.berater-2026-10-20T09:00"))

  private val now                = LocalDateTime.of(2026, 10, 6, 12, 0)
  private def reservation(status: ReservationStatus = ReservationStatus.reserved, until: LocalDateTime = now.plusMinutes(20)) =
    Reservation(in.reservationId, in.appointment, in.contact, None, status, until)
  private def verify(r: Option[Reservation], i: In = in) =
    BookAppointmentWorker.verify(i, r, now).left.map(_.errorMsg)

  test("verify - only with its valid reservation"):
    assertEquals(verify(Some(reservation())), Right(()))
    assert(verify(None).left.exists(_.contains("unknown")))
    assert(verify(Some(reservation(ReservationStatus.booked))).left.exists(_.contains("booked")))
    assert(verify(Some(reservation(until = now.minusMinutes(1)))).left.exists(_.contains("expired")))

  test("verify - the same slot and e-mail as reserved"):
    val otherSlot = in.copy(appointment = in.appointment.copy(start = in.appointment.start.plusHours(1)))
    assert(verify(Some(reservation()), otherSlot).left.exists(_.contains("another slot")))
    val otherMail = in.copy(contact = in.contact.copy(email = "someone@else.ch"))
    assert(verify(Some(reservation()), otherMail).left.exists(_.contains("another e-mail")))
    val upper     = in.copy(contact = in.contact.copy(email = in.contact.email.toUpperCase))
    assertEquals(verify(Some(reservation()), upper), Right(()))

end BookAppointmentWorkerTest
