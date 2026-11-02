package democompany.patterns
package domain.stopWithError.v1

/** Example process for the pattern «Prozess mit Fehler stoppen»: a gateway on `blocked` - blocked
  * ends at an end event that stops the process with an incident whose message describes the
  * problem (C7: Groovy exception after asyncBefore, C8: FEEL assert in an output mapping). Not
  * blocked, a gateway on `limitExceeded` leads to an intermediate throw event with the pattern - after
  * a process instance modification past it, the process goes on to `succeeded`.
  */
object StopWithError extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-stopWithErrorV1"
  val descr: String =
    """Beispiel für das Pattern «Prozess mit Fehler stoppen»: ist der Kunde gesperrt (`blocked`), bleibt
      |der Prozess am Ende mit einem Incident stehen - Meldung «Kunde <Name> ist gesperrt.». Ist das Limit
      |überschritten (`limitExceeded`), bleibt er an einem Zwischenereignis stehen - «Limit von <Name>
      |überschritten.»; schieben DevOps ihn danach weiter, endet er mit `succeeded`. Sonst endet er gleich
      |mit `succeeded`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String,
      @description("Ist der Kunde gesperrt? Ja: der Prozess bleibt am Ende mit einem Incident stehen.")
      blocked: Boolean = false,
      @description("Ist das Limit überschritten? Ja: der Prozess bleibt an einem Zwischenereignis mit einem Incident stehen.")
      limitExceeded: Boolean = false
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      blocked: Boolean = false,
      limitExceeded: Boolean = false
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
