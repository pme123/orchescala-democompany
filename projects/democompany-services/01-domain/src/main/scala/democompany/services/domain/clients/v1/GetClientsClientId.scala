package democompany.services
package domain
package clients.v1

import schema.*

object GetClientsClientId extends ClientsV1:

  val topicName     = "democompany-services-clientsV1-GetClientsClientId"
  val descr: String = "Get the Client by Id"

  val path = "GET: clients/clientId"

  case class In(
      clientId: Long
  )
  object In:
    given ApiSchema[In]     = deriveApiSchema
    given InOutCodec[In]    = deriveInOutCodec
    lazy val example        = In(
      clientId = defaultClientId
    )
    lazy val exampleMinimal = example // .copy(..=None)
  end In

  case class Out(
      client: Client,
      etag: String,
      email: Option[String]
  )
  object Out:
    given ApiSchema[Out]    = deriveApiSchema
    given InOutCodec[Out]   = deriveInOutCodec
    lazy val example        = Out(
      client = Client.example,
      etag = defaultEtag,
      email = Some("john.doe@example.com")
    )
    lazy val exampleMinimal = example.copy(client = Client.exampleMinimal, email = None)
  end Out

  type ServiceIn  = NoInput // if no input is needed
  type ServiceOut = Client  // if no output is needed

  object ServiceIn:
    lazy val example        = NoInput() // or ...example
    lazy val exampleMinimal = example   // copy(..=None)

  object ServiceOut:
    lazy val example        = Client.example // or ...example
    lazy val exampleMinimal = Client.exampleMinimal
    lazy val mock           = MockedServiceResponse.success200(example, etagHeaderMock)
    lazy val mockMinimal    = MockedServiceResponse.success200(exampleMinimal, etagHeaderMock)
  end ServiceOut

  lazy val example = serviceTask(
    In.example,
    Out.example,
    ServiceOut.mock,
    ServiceIn.example
  )

  lazy val exampleMinimal = serviceTask(
    In.exampleMinimal,
    Out.exampleMinimal,
    ServiceOut.mockMinimal,
    ServiceIn.exampleMinimal
  )
end GetClientsClientId
