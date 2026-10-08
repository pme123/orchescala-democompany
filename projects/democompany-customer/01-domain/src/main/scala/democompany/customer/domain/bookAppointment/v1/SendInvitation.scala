package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Schickt die Einladung (.ics) an Kunde und Berater - so steht der Termin in Outlook. */
object SendInvitation extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-SendInvitation"
  val descr: String = "Schickt die Einladung (.ics) an Kunde und Berater - so steht der Termin in Outlook."

  /** Schickt die Einladung (.ics) an Kunde und Berater - so steht der Termin in Outlook. */
  case class In(
      @description("Der gebuchte Termin")
      appointment: Appointment,
      @description("Kontaktdaten des Kunden")
      contact: Contact
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      appointment = Appointment.example,
      contact = Contact.example
    )
    lazy val exampleMinimal = example
  end In

  /** Schickt die Einladung (.ics) an Kunde und Berater - so steht der Termin in Outlook. */
  case class Out(
      @description("Zeitpunkt des Versands")
      sentAt: LocalDateTime
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      sentAt = LocalDateTime.now()
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end SendInvitation
