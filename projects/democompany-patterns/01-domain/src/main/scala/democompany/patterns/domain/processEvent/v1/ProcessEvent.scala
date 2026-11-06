package democompany.patterns
package domain.processEvent.v1

/** The event process of the example for «Prozess-Event senden»: «Event melden» passes the event to
  * the service `PostEvent` (democompany-services) and ends. This is the part each company sets up
  * its own way - another one reports to another system or does more steps. Its inputs are what the
  * pattern passes.
  */
object ProcessEvent extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-processEventV1"
  val descr: String =
    """Event-Prozess für das Pattern «Prozess-Event senden»: «Event melden» gibt das Event an den Service
      |`PostEvent` (democompany-services) weiter, dann endet er mit `succeeded`. Was mit einem Event
      |geschieht, legt jede Firma in ihrem Event-Prozess fest. Die Eingaben sind die, die das Pattern
      |mitgibt; zurück gibt er nichts.
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
