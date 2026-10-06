package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
import democompany.customer.worker.appointments.{AppointmentTexts, AppointmentsConfig}

/** Sets the flat variables for the mails and the appointment rules. */
class BookAppointmentWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:

  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    InitIn(
      email = in.contact.email,
      customerName = AppointmentTexts.customerName(in.contact),
      advisorEmail = in.appointment.advisorEmail,
      topic = in.appointment.topic.toString,
      channel = in.appointment.channel.toString,
      appointmentText = AppointmentTexts.of(in.appointment),
      verificationLink = AppointmentsConfig.verificationLink(in.reservationId),
      confirmLink = AppointmentsConfig.confirmLink(in.reservationId)
    )

end BookAppointmentWorker
