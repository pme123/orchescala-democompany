package democompany.services.domain.clients.v1.schema

enum Gender:
  case Male, Female, Unknown
object Gender:
  given ApiSchema[Gender]  = deriveApiSchema
  given InOutCodec[Gender] = deriveInOutCodec

case class Client(
    id: Long,
    firstName: String,
    lastName: String,
    email: Option[String],
    tel: Option[String],
    address: Address,
    gender: Gender,
    fullName: String,
    salutation: String
)

object Client:
  given ApiSchema[Client]  = deriveApiSchema
  given InOutCodec[Client] = deriveInOutCodec

  def apply(
      id: Long,
      firstName: String,
      lastName: String,
      email: Option[String],
      tel: Option[String],
      address: Address,
      gender: Gender
  ): Client = Client(
    id,
    firstName,
    lastName,
    email,
    tel,
    address,
    gender,
    s"$firstName $lastName",
    gender match {
      case Gender.Male   => "Mr."
      case Gender.Female => "Ms."
      case Gender.Unknown => ""
    }
  )

  lazy val example         = Client(
    id = 1,
    firstName = "John",
    lastName = "Doe",
    email = Some("john.doe@example.com"),
    tel = Some("+411234567890"),
    address = Address.example,
    gender = Gender.Male
  )

  lazy val exampleMinimal  = example.copy(email = None, tel = None, address = Address.exampleMinimal)
end Client
