package democompany.customer.domain.bookAppointment.v1.schema


/** Kontaktdaten aus dem Formular der Homepage */
case class Contact(
    @description("Vorname")
    firstName: String,
    @description("Nachname")
    lastName: String,
    @description("E-Mail - für den Opt-in und die Einladung")
    email: String,
    @description("Telefon")
    phone: Option[String],
    @description("Kunden-Nr. - nur ein Hinweis, verbindlich ordnet der Berater zu")
    customerNo: Option[String]
)

object Contact:
  given ApiSchema[Contact]  = deriveApiSchema
  given InOutCodec[Contact] = deriveInOutCodec

  lazy val example = Contact(
    firstName = "Peter",
    lastName = "Muster",
    email = "peter.muster@example.ch",
    phone = Some("+41 79 123 45 67"),
    customerNo = Some("100200")
  )
  lazy val exampleMinimal = example.copy(
    phone = None,
    customerNo = None
  )
end Contact
