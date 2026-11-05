package democompany.patterns
package domain.processEvent.v1

/** The event process of the example for «Prozess-Event senden» - a dummy: it takes the event and
  * ends. A real one would report it, e.g. to another system. Its inputs are what the pattern
  * passes.
  */
object ProcessEvent extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-processEventV1"
  val descr: String =
    """Dummy-Event-Prozess für das Pattern «Prozess-Event senden»: nimmt das Event entgegen und endet mit
      |`succeeded`. Ein echter Event-Prozess würde es z. B. einem Fremdsystem melden. Die Eingaben sind
      |die, die das Pattern mitgibt; zurück gibt er nichts.
      |""".stripMargin

  case class In(
      @description("Was passiert ist, z. B. `started`, `finished`.")
      eventKey: String,
      @description("Die ID des Prozesses, der das Event meldet.")
      processDefinitionKey: String,
      @description("Die Instanz des Prozesses, der das Event meldet.")
      processInstanceId: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      eventKey = "started",
      processDefinitionKey = "democompany-patterns-sendProcessEventV1",
      processInstanceId = "4711"
    )
  end In

  case class Out(
      processStatus: ProcessStatus
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(processStatus = ProcessStatus.succeeded)
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end ProcessEvent
