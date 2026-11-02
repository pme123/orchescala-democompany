package democompany.patterns
package domain.processStatus.v1

/** Example process for the pattern «Prozess-Status am Ende»: each end sets `processStatus` - so
  * the caller (and the simulation) sees how the process ended. Here a gateway on `approved` leads
  * to the end «succeeded» or «canceled».
  */
object ProcessStatusExample extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-processStatusV1"
  val descr: String =
    """Beispiel für das Pattern «Prozess-Status am Ende»: jedes Ende setzt `processStatus` -
      |so sieht der Aufrufer (und die Simulation), wie der Prozess ausgegangen ist.
      |Ist der Antrag bewilligt (`approved`), endet der Prozess mit `succeeded`, sonst mit `canceled`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String,
      @description("Ist der Antrag bewilligt? Ja: Ende «succeeded», nein: Ende «canceled».")
      approved: Boolean = true
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class Out(
      @description("Wie der Prozess ausgegangen ist - gesetzt vom Ende, an dem er angekommen ist.")
      processStatus: ProcessStatus,
      customerName: String,
      approved: Boolean = true
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end ProcessStatusExample
