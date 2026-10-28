package democompany.patterns
package domain.identity.v1

/** A service call with the identity the process has at that point - in the example always mocked
  * (`_servicesMocked`): what counts is that the ServiceWorker verifies the signature of
  * `_identityCorrelation` first (after «Identität übernehmen» it is the copied actor).
  *
  * It also adds the username of that identity to `checkedUsernames` - so the process shows which
  * identity each check had. The BPMN maps the username in, as a ServiceWorker can't read the
  * identity itself.
  */
object CheckIdentity extends CompanyBpmnServiceTaskDsl:

  val topicName     = "democompany-patterns-identityV1-CheckIdentity"
  val descr: String =
    "Identität prüfen - ein Service-Aufruf mit der Identität des Prozesses; der ServiceWorker prüft vorher die Signatur von `_identityCorrelation`."

  final val serviceLabel = "Patterns Identity 1.0"
  val serviceVersion     = "1.0"

  val path = "GET: identity"

  case class In(
      customerName: String,
      @description("Benutzername der Identität des Prozesses - das BPMN mappt ihn aus `_identityCorrelation`.")
      identityUsername: String,
      @description("Die Benutzernamen der bisherigen Prüfungen.")
      checkedUsernames: Option[Seq[String]] = None
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      customerName = Identity.In.example.customerName,
      identityUsername = "actor"
    )
  end In

  case class Out(
      @description("Der Service wurde mit der (geprüften) Identität des Prozesses aufgerufen.")
      identityChecked: Boolean = true,
      @description("Die Benutzernamen aller Prüfungen bis hier, in ihrer Reihenfolge.")
      checkedUsernames: Seq[String] = Seq("actor")
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
end CheckIdentity
