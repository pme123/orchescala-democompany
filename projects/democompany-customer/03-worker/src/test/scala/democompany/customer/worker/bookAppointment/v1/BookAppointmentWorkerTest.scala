package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
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

end BookAppointmentWorkerTest
