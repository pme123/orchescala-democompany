package democompany.services.domain.clients.v1.schema

case class Address(
    street: String,
    number: String,
    zip: String,
    city: String,
    country: String,
    additionalInfo: Option[String]
)

object Address:
  given ApiSchema[Address]  = deriveApiSchema
  given InOutCodec[Address] = deriveInOutCodec
  lazy val example          = Address(
    street = "Main Street",
    number = "123",
    zip = "12345",
    city = "Anytown",
    country = "Germany",
    additionalInfo = Some("Additional Info")
  )
  lazy val exampleMinimal   = example.copy(additionalInfo = None)
end Address
