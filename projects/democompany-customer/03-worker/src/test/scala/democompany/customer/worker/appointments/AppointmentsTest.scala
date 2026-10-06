package democompany.customer
package worker.appointments

import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Contact, Topic}
import democompany.customer.worker.bookAppointment.v1.SendMailWorker

//sbt worker/testOnly *AppointmentsTest
class AppointmentsTest extends munit.FunSuite:

  private val appointment = Appointment.example.copy(
    start = LocalDateTime.of(2026, 10, 20, 9, 0),
    end = LocalDateTime.of(2026, 10, 20, 10, 30),
    topic = Topic.mortgage,
    channel = Channel.video
  )
  private val contact = Contact.example.copy(firstName = "Peter", lastName = "Muster", email = "peter.muster@example.ch")

  test("the appointment as text"):
    assertEquals(AppointmentTexts.of(appointment), "Di 20.10.2026, 09:00-10:30, Hypothek, Video")

  test("invitation - an appointment Outlook can accept, in UTC"):
    val ics = Invitation.ics("anna.berater-2026-10-20T09:00", appointment, contact, LocalDateTime.of(2026, 10, 6, 12, 0))
    assert(ics.contains("METHOD:REQUEST\r\n"), ics)
    // Zurich is UTC+2 in October
    assert(ics.contains("DTSTART:20261020T070000Z\r\nDTEND:20261020T083000Z\r\n"), ics)
    assert(ics.contains("ATTENDEE;CN=Peter Muster;RSVP=TRUE:mailto:peter.muster@example.ch"), ics)
    assert(ics.contains("ORGANIZER;CN=Anna Berater:mailto:anna.berater@democompany.ch"), ics)
    assert(ics.endsWith("END:VCALENDAR\r\n"), ics)

  test("invitation - commas and semicolons are escaped"):
    val ics = Invitation.ics("x", appointment.copy(advisorName = "Berater, Anna"), contact, LocalDateTime.now())
    assert(ics.contains("ORGANIZER;CN=Berater\\, Anna:"), ics)

  test("a mail text from the BPMN - `\\n` is a line break"):
    assertEquals(SendMailWorker.text("Guten Tag\\n\\nGrüsse"), "Guten Tag\n\nGrüsse")

end AppointmentsTest
