package democompany.patterns
package domain.sendProcessEvent.v1

/** Example process for the pattern «Prozess-Event senden»: after «Init Process» it reports
  * `started`, at the end `finished` - each time a call activity calls the event process
  * `democompany-patterns-processEventV1` with the event, the process id and the instance. The event
  * process gets only these inputs (and the mocks and the identity) and gives nothing back; it
  * reports the event to the service `PostEvent`.
  */
object SendProcessEvent extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-sendProcessEventV1"
  val descr: String =
    """Beispiel für das Pattern «Prozess-Event senden»: der Prozess meldet zu Beginn `started` und am Ende
      |`finished` an den Event-Prozess `democompany-patterns-processEventV1` - je eine Call Activity mit
      |dem Event, der Prozess-ID und der Instanz. Der Event-Prozess gibt es an den Service `PostEvent`
      |weiter. Danach endet der Prozess mit `succeeded`.
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

  case class Out(
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
    Out.example
  )
end SendProcessEvent
