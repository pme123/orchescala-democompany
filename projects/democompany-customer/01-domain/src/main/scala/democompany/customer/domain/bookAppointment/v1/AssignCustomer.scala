package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent - und hält die
  * Reservierung fest: nach dem Opt-in läuft sie nicht mehr ab, bis der Berater entschieden hat.
  */
object AssignCustomer extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-AssignCustomer"
  val descr: String = "Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent - und hält die Reservierung fest."

  /** Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent. */
  case class In(
      @description("Kontaktdaten aus dem Formular")
      contact: Contact,
      @description("Reservierung")
      reservationId: String,
      @description("Das Geheimnis der Reservierung (aus reserveSlot) - nur die eigene wird geändert")
      token: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      contact = Contact.example,
      reservationId = "anna.berater-2026-10-20T09:00",
      token = "0b1c9a4e-7a43-4f0e-9d39-3a3f6c2d8e11"
    )
    lazy val exampleMinimal = example
  end In

  /** Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent. */
  case class Out(
      @description("Kunde oder Interessent")
      customerStatus: CustomerStatus,
      @description("Kunden-ID, wenn gefunden")
      clientId: Option[String],
      @description("Name laut Kundenstamm, wenn gefunden")
      customerName: Option[String]
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      customerStatus = CustomerStatus.customer,
      clientId = Some("100200"),
      customerName = Some("Beispiel")
    )
    lazy val exampleMinimal = example.copy(
      clientId = None,
      customerName = None
    )
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end AssignCustomer
