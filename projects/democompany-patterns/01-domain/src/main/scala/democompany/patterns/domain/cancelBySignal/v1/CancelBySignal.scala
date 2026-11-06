package democompany.patterns
package domain.cancelBySignal.v1

/** Example process for the pattern «Abbruch per Signal»: an interrupting event sub process with the
  * signal `cancel-<process instance>` - it cancels the process wherever it waits and ends with
  * `processStatus = canceled`. Without the signal, «Antrag bearbeiten» is done and the process ends
  * with `succeeded`.
  *
  * C7 names the signal `cancel-${execution.processInstanceId}`, C8 (from 8.10)
  * `="cancel-" + string(camunda.processInstance.key)` - both are bound to the instance from its start.
  */
object CancelBySignal extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-cancelBySignalV1"
  val descr: String =
    """Beispiel für das Pattern «Abbruch per Signal»: ein unterbrechender Ereignis-Subprozess wartet auf
      |das Signal `cancel-<Instanz>`. Kommt es, bricht er den Prozess ab - egal, wo er gerade wartet - und
      |endet mit `canceled`. Ohne Signal wird «Antrag bearbeiten» erledigt, und der Prozess endet mit
      |`succeeded`. Auf C8 braucht das Pattern Camunda 8.10 (`camunda.processInstance.key`).
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
      @description("`true`, sobald der InitWorker durch ist - ab dann wartet der Prozess an «Antrag bearbeiten», und das Abbruch-Signal darf kommen (die Simulation wartet darauf).")
      waitForSignal: Boolean = true
  )
  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec
    lazy val example         = InitIn()
  end InitIn

  case class Out(
      @description("Wie der Prozess ausgegangen ist - `canceled`, wenn das Abbruch-Signal kam.")
      processStatus: ProcessStatus,
      customerName: String
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
end CancelBySignal
