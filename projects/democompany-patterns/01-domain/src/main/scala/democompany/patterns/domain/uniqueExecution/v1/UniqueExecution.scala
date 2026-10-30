package democompany.patterns
package domain.uniqueExecution.v1

/** Example process for the pattern «Einmalige Ausführung».
  *
  * The process starts with a message (name = process id) and a `businessKey`. While it runs, a
  * non-interrupting event sub process catches the same message - so a second start with the same
  * `businessKey` ends up in the running instance (`alreadyRunning = true`) instead of starting a
  * second one. In C8 the message subscription of the event sub process correlates with
  * `=businessKey`.
  */
object UniqueExecution extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-uniqueExecutionV1"
  val descr: String =
    """Beispiel für das Pattern «Einmalige Ausführung».
      |Der Prozess startet über eine Nachricht (Name = Prozess-ID) mit einem `businessKey`. Solange er
      |läuft, fängt ein nicht unterbrechender Ereignis-Subprozess dieselbe Nachricht - ein zweiter Start
      |mit demselben `businessKey` landet in der laufenden Instanz (`alreadyRunning = true`) statt eine
      |zweite zu starten.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class InitIn(
      @description("`false` beim Start - der Ereignis-Subprozess «Prozess läuft schon» setzt ihn auf `true`.")
      alreadyRunning: Boolean = false
  )
  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec
    lazy val example         = InitIn()
  end InitIn

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Kam die Start-Nachricht ein zweites Mal, während der Prozess lief?")
      alreadyRunning: Boolean = false
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
    Out.example,
    InitIn.example
  )
end UniqueExecution
