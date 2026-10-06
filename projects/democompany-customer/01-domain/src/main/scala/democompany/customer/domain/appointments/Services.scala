package democompany.customer.domain.appointments

import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Contact, Topic}

/** The services of the area - without process, called synchronously over the gateway
  * (`/worker/{topic}`) by the page «Termin buchen».
  */

/** The free slots for a topic and channel: what the appointment rules allow and Outlook has free,
  * without what is reserved or booked over the app.
  */
object FreeSlots extends CompanyBpmnCustomTaskDsl:
  val topicName     = "democompany-customer-freeSlots"
  val descr: String = "Freie Termine für Thema und Kanal - nach den Terminregeln, aus Outlook, ohne Reservierungen."

  case class In(
      @description("Thema")
      topic: Topic = Topic.advice,
      @description("Kanal")
      channel: Channel = Channel.branch,
      @description("Ab welchem Tag - ohne Angabe heute")
      from: Option[LocalDate] = None,
      @description("Wie viele Tage")
      days: Int = 14
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

  case class Out(
      @description("Die freien Termine, nach Beginn")
      slots: Seq[Appointment] = Seq(Appointment.example)
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

  lazy val example = customTask(In(), Out())
end FreeSlots

/** Reserves a slot before the process starts - so the customer sees at once if it is taken. */
object ReserveSlot extends CompanyBpmnCustomTaskDsl:
  val topicName     = "democompany-customer-reserveSlot"
  val descr: String = "Reserviert einen freien Termin - ist er schon vergeben, ein Fehler «leider vergeben»."

  case class In(
      @description("Der gewählte Termin")
      appointment: Appointment = Appointment.example,
      @description("Kontaktdaten")
      contact: Contact = Contact.example,
      @description("Bemerkung")
      remark: Option[String] = None
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

  case class Out(
      @description("Die Reservierung `<advisor>-<start>` - damit startet der Prozess bookAppointment")
      reservationId: String = "anna.berater-2026-10-20T09:00",
      @description("Bis wann sie ohne Opt-in gilt")
      reservedUntil: LocalDateTime = LocalDateTime.of(2026, 10, 6, 12, 30)
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

  lazy val example = customTask(In(), Out())
end ReserveSlot
