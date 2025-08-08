package democompany.services.domain.clients.v1.schema

case class Client(
    id: Long,
    firstName: String,
    lastName: String,
    email: Option[String],
    address: Address
)

object Client:
  given ApiSchema[Client]  = deriveApiSchema
  given InOutCodec[Client] = deriveInOutCodec
  lazy val example         = Client(
    id = 1,
    firstName = "John",
    lastName = "Doe",
    email = Some("john.doe@example.com"),
    address = Address.example
  )
  lazy val exampleMinimal  = example.copy(email = None)
end Client
