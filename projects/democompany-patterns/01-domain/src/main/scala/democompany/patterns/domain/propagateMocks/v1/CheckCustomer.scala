package democompany.patterns
package domain.propagateMocks.v1

/** A service call in the sub process - to a service that does not exist: it only succeeds if the
  * caller passed `_servicesMocked`. Before it mocks, the ServiceWorker verifies the signature of
  * the `_identityCorrelation` that came from the caller.
  */
object CheckCustomer extends CompanyBpmnServiceTaskDsl:

  val topicName     = "democompany-patterns-propagateMocksSubV1-CheckCustomer"
  val descr: String =
    "Kunde prüfen - ein Service-Aufruf im Teilprozess; gelingt nur mit `_servicesMocked` vom Aufrufer."

  final val serviceLabel = "Patterns Customer 1.0"
  val serviceVersion     = "1.0"

  val path = "GET: customer"

  case class In(
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = PropagateMocks.In.example.customerName)
  end In

  case class Out(
      @description("Der Kunde wurde geprüft.")
      customerChecked: Boolean = true
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out()
  end Out

  type ServiceIn  = NoInput
  type ServiceOut = NoOutput

  object ServiceIn:
    lazy val example = NoInput()

  object ServiceOut:
    lazy val example = NoOutput()
    lazy val mock    = MockedServiceResponse.success204
  end ServiceOut

  lazy val example = serviceTask(
    In.example,
    Out.example,
    ServiceOut.mock,
    ServiceIn.example
  )
end CheckCustomer
