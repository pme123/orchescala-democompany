package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent. */
object AssignCustomer extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-AssignCustomer"
  val descr: String = "Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent."

  /** Ordnet die Anfrage einem Kunden zu (Kunden-Nr. oder E-Mail), sonst Interessent. */
  case class In(
      @description("Kontaktdaten aus dem Formular")
      contact: Contact
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      contact = Contact.example
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
