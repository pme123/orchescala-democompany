package democompany.services
package domain.clients.v1

object GetClientsClientId extends ClientsV1:

  val topicName = "democompany-services-clientsV1-GetClientsClientId"
  val descr: String = ""

  val path = "GET: clients/clientId"
          
  case class In(
    clientId: Long
  )
  object In:
    given ApiSchema[In] = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example = In(
      clientId = defaultClientId
    )
    lazy val exampleMinimal = example //.copy(..=None)


  case class Out(//TODO output variables
            
  )
  object Out:
    given ApiSchema[Out] = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example = Out()
    lazy val exampleMinimal = example //.copy(..=None)


  type ServiceIn = NoInput // if no input is needed
  type ServiceOut = NoOutput // if no output is needed

  object ServiceIn:
    lazy val example = NoInput() // or ...example
     lazy val exampleMinimal = example//copy(..=None)

  object ServiceOut:
     lazy val example = NoOutput() // or ...example
     lazy val exampleMinimal = example //.copy(..=None)
     lazy val mock = MockedServiceResponse.success204 // or MockedServiceResponse.success200(example)
     lazy val mockMinimal = MockedServiceResponse.success204 // or MockedServiceResponse.success200(exampleMinimal)

  lazy val example = serviceTask(
    In.example,
    Out.example
    ,
    ServiceOut.mock,
    ServiceIn.example
  )

  lazy val exampleMinimal = serviceTask(
    In.exampleMinimal,
    Out.exampleMinimal
    ,
    ServiceOut.mockMinimal,
    ServiceIn.exampleMinimal
  )
end GetClientsClientId