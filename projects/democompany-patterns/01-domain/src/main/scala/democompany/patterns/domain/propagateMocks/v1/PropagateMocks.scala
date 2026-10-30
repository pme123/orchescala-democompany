package democompany.patterns
package domain.propagateMocks.v1

import democompany.services.domain.clients.v1.defaultClientId
import democompany.services.domain.clients.v1.schema.Client

/** Example process for the pattern «Mocks und Identität weitergeben».
  *
  * The call activity «Teilprozess aufrufen» passes `_servicesMocked`, `_mockedWorkers` and
  * `_identityCorrelation` to the sub process - only then do the mocks and the identity work there
  * too. In C7 a call activity passes only what it names; in C8 the example turns off passing all
  * variables (`propagateAllParentVariables="false"`), so both engines pass the same.
  */
object PropagateMocks extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-propagateMocksV1"
  val descr: String =
    """Beispiel für das Pattern «Mocks und Identität weitergeben».
      |Die Call Activity «Teilprozess aufrufen» gibt `_servicesMocked`, `_mockedWorkers` und
      |`_identityCorrelation` an den Teilprozess weiter - so greifen Mocks und Identität auch dort.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String,
      @description("Id des Kunden - der Teilprozess lädt ihn mit dem Service `GetClientsClientId`.")
      clientId: Long
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster", clientId = defaultClientId)
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Der `processStatus`, mit dem der Teilprozess endete.")
      subProcessStatus: ProcessStatus,
      @description("Der Kunde, den der Teilprozess geladen hat.")
      client: Option[Client] = None
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName,
      subProcessStatus = PropagateMocksSub.Out.example.processStatus,
      client = Some(PropagateMocksSub.Out.example.client)
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end PropagateMocks
