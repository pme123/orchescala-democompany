package democompany.patterns
package domain.initProcess.v1

/** Example process for the pattern «Init Process»: the first step is a service task with the
  * process id as topic - the InitWorker validates the In, sets the defaults of the InConfig and
  * delivers mocked outputs (`output-mocked` -> link -> end with `processStatus = output-mocked`).
  */
object InitProcess extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-initProcessV1"
  val descr: String =
    """Beispiel für das Pattern «Init Process»: der erste Schritt ist ein Service-Task mit der Prozess-ID als Topic.
      |Der InitWorker prüft das In, setzt die Vorgaben aus dem InConfig und liefert gemockte Ausgaben -
      |ist der Prozess gemockt, endet er über den Pfad `output-mocked` mit `processStatus = output-mocked`.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String,
      @description(
        "A way to override process configuration.\n\n**SHOULD NOT BE USED on Production!**"
      )
      inConfig: Option[InConfig] = None
  ) extends WithConfig[InConfig]:
    lazy val defaultConfig = InConfig()
  end In
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class InConfig(
      @description("Die Anrede, mit der der InitWorker die Begrüssung bildet.")
      greeting: String = "Grüezi"
  )
  object InConfig:
    given ApiSchema[InConfig]  = deriveApiSchema
    given InOutCodec[InConfig] = deriveInOutCodec

  // the variables the InitWorker sets (customInit)
  case class InitIn(
      @description("Begrüssung aus der Anrede im InConfig und dem Namen des Kunden.")
      greetingMessage: String
  )
  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec
    lazy val example         = InitIn(greetingMessage = "Grüezi Peter Muster")
  end InitIn

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Die Vorgaben des InConfig - vom InitWorker gesetzt, wenn das In keine hat.")
      inConfig: InConfig,
      greetingMessage: String
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName,
      inConfig = InConfig(),
      greetingMessage = InitIn.example.greetingMessage
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example,
    InitIn.example
  )
end InitProcess
