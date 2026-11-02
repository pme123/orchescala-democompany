package democompany.patterns
package domain.stopWithError.v1

/** Example process for the pattern «Prozess mit Fehler stoppen»: a gateway on `blocked` - not
  * blocked ends with `succeeded`, blocked ends at an end event that stops the process with an
  * incident whose message describes the problem (C7: Groovy exception after asyncBefore, C8: FEEL
  * assert in an output mapping).
  */
object StopWithError extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-stopWithErrorV1"
  val descr: String =
    """Beispiel für das Pattern «Prozess mit Fehler stoppen»: ist der Kunde gesperrt (`blocked`), bleibt
      |der Prozess mit einem Incident stehen - Meldung «Kunde <Name> ist gesperrt.». Sonst endet er mit
      |`succeeded`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String,
      @description("Ist der Kunde gesperrt? Ja: der Prozess bleibt mit einem Incident stehen.")
      blocked: Boolean = false
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      blocked: Boolean = false
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
end StopWithError
