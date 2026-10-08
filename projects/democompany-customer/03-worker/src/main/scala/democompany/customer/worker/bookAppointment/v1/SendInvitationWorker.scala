package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.SendInvitation.*
import democompany.customer.worker.appointments.{AppointmentTexts, Invitation}

/** The invitation to customer and advisor - with the appointment as iCalendar, so it is in Outlook. */
class SendInvitationWorker extends CompanyMailWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    val now  = LocalDateTime.now()
    val a    = in.appointment
    val text =
      s"""Guten Tag ${AppointmentTexts.customerName(in.contact)}
         |
         |Ihr Termin ist gebucht: ${AppointmentTexts.of(a)}, mit ${a.advisorName}.
         |Die Einladung liegt bei.
         |
         |Freundliche Grüsse
         |Demo Company""".stripMargin
    val ics  = Invitation.ics(SendInvitationWorker.uid(a), a, in.contact, now)
    sendMail(
      Seq(in.contact.email, a.advisorEmail),
      s"Termin: ${AppointmentTexts.topic(a.topic)} am ${AppointmentTexts.of(a).takeWhile(_ != ',')}",
      text,
      Seq(MailPart("text/calendar; charset=UTF-8; method=REQUEST", ics, Some("termin.ics")))
    ).as(Out(now))
  end runWorkZIO

end SendInvitationWorker

object SendInvitationWorker:
  def uid(a: democompany.customer.domain.bookAppointment.v1.schema.Appointment): String =
    s"${a.advisorId}-${a.start}"
