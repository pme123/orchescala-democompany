package democompany.patterns
package domain.propagateMocks.v1

/** The sub process of «Mocks und Identität weitergeben» - it only knows what the call activity
  * passes: the mocks and the identity of its parent.
  */
object PropagateMocksSub extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-propagateMocksSubV1"
  val descr: String =
    """Teilprozess zum Pattern «Mocks und Identität weitergeben».
      |«Kunde prüfen» ruft einen Service auf, den es nicht gibt - er gelingt nur, wenn
      |`_servicesMocked` vom Aufrufer kommt; vorher prüft der ServiceWorker die weitergegebene
      |`_identityCorrelation`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - vom Aufrufer.")
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = PropagateMocks.In.example.customerName)
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Der Kunde wurde geprüft - aus «Kunde prüfen».")
      customerChecked: Boolean
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName,
      customerChecked = CheckCustomer.Out.example.customerChecked
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end PropagateMocksSub
