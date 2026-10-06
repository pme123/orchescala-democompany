package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

object BookAppointment extends CompanyBpmnProcessDsl:

  val processName = "democompany-customer-bookAppointmentV1"
  val descr: String = ""

  /** Die Anfrage von der Homepage - der Termin ist schon reserviert. */
  case class In(
      @description("Reservierung `<advisor>-<start>` - entsteht vor dem Start (Service reserveSlot)")
      reservationId: String,
      @description("Der gewählte Termin")
      appointment: Appointment,
      @description("Kontaktdaten")
      contact: Contact,
      @description("Bemerkung")
      remark: Option[String],
      @description("A way to override process configuration.\n\n**SHOULD NOT BE USED on Production!**")
      inConfig: Option[InConfig] = None
  ) extends WithConfig[InConfig]:
    lazy val defaultConfig = InConfig()
  end In

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      reservationId = "anna.berater-2026-10-20T09:00",
      appointment = Appointment.example,
      contact = Contact.example,
      remark = Some("Bitte mit Parkplatz")
    )
    lazy val exampleMinimal = example.copy(
      remark = None
    )
  end In

  /** Vom Init-Worker gesetzt */
  case class InitIn(
      @description("E-Mail des Kunden (aus contact)")
      email: String,
      @description("Vorname und Name (aus contact)")
      customerName: String,
      @description("E-Mail des Beraters (aus appointment)")
      advisorEmail: String,
      @description("Thema - für die Terminregeln")
      topic: String,
      @description("Kanal - für die Terminregeln")
      channel: String,
      @description("Der Termin als Text für die Mails")
      appointmentText: String,
      @description("Link für den Opt-in")
      verificationLink: String,
      @description("Link auf den Task «Termin bestätigen»")
      confirmLink: String
  )

  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec

    lazy val example = InitIn(
      email = "peter.muster@example.ch",
      customerName = "Peter Muster",
      advisorEmail = "anna.berater@democompany.ch",
      topic = "mortgage",
      channel = "branch",
      appointmentText = "Mo 20.10.2026, 09:00-10:30, Hypothek, Filiale",
      verificationLink = "http://localhost:8888/app/democompany-customer/appointments/verified?reservation=anna.berater-2026-10-20T09:00",
      confirmLink = "http://localhost:8888/app/democompany-customer/appointments/confirm?process=123"
    )
    lazy val exampleMinimal = example
  end InitIn

  case class InConfig(
      @description("So lange gilt die Reservierung ohne Opt-in")
      timerVerifyEmail: Iso8601Duration = "PT30M",
      @description("Erinnerung an den Berater, wenn der Task offen ist")
      timerConfirmReminder: Iso8601Duration = "P1D"
  )

  object InConfig:
    given ApiSchema[InConfig]  = deriveApiSchema
    given InOutCodec[InConfig] = deriveInOutCodec
  end InConfig

  /** Ergebnis der Anfrage */
  case class Out(
      @description("Reservierung")
      reservationId: String,
      @description("Termin gebucht")
      booked: Boolean,
      @description("Kunde oder Interessent")
      customerStatus: Option[CustomerStatus]
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      reservationId = "anna.berater-2026-10-20T09:00",
      booked = true,
      customerStatus = Some(CustomerStatus.customer)
    )
    lazy val exampleMinimal = example.copy(
      customerStatus = None
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example,
    InitIn.example
  )

  lazy val exampleMinimal = process(
    In.exampleMinimal,
    Out.exampleMinimal,
    InitIn.exampleMinimal
  )
end BookAppointment
