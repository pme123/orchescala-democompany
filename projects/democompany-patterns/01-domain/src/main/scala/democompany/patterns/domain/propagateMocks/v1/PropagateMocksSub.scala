package democompany.patterns
package domain.propagateMocks.v1

import democompany.services.domain.clients.v1.GetClientsClientId
import democompany.services.domain.clients.v1.schema.Client

/** The sub process of «Mocks und Identität weitergeben» - it only knows what the call activity
  * passes: the mocks and the identity of its parent. «Kunde laden» calls the service
  * `GetClientsClientId` of democompany-services.
  */
object PropagateMocksSub extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-propagateMocksSubV1"
  val descr: String =
    """Teilprozess zum Pattern «Mocks und Identität weitergeben».
      |«Kunde laden» ruft den Service `GetClientsClientId` (democompany-services) auf - gemockt,
      |wenn `_servicesMocked` vom Aufrufer kommt; vorher prüft der ServiceWorker die weitergegebene
      |`_identityCorrelation`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - vom Aufrufer.")
      customerName: String,
      @description("Id des Kunden - vom Aufrufer.")
      clientId: Long
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      customerName = PropagateMocks.In.example.customerName,
      clientId = PropagateMocks.In.example.clientId
    )
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Der Kunde - aus «Kunde laden».")
      client: Client
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName,
      client = GetClientsClientId.Out.example.client
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end PropagateMocksSub
