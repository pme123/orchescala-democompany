package democompany.customer.domain.updateAvailability.v1.schema


/** Kundenberaterin oder Kundenberater */
case class Advisor(
    @description("Benutzer-ID im IdP")
    id: String,
    @description("Name")
    name: String,
    @description("E-Mail für Benachrichtigungen und Einladungen")
    email: String
)

object Advisor:
  given ApiSchema[Advisor]  = deriveApiSchema
  given InOutCodec[Advisor] = deriveInOutCodec

  lazy val example = Advisor(
    id = "anna.berater",
    name = "Anna Berater",
    email = "anna.berater@democompany.ch"
  )
  lazy val exampleMinimal = example
end Advisor
