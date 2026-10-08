package democompany.customer.domain.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.schema.*

object UpdateAvailability extends CompanyBpmnProcessDsl:

  val processName = "democompany-customer-updateAvailabilityV1"
  val descr: String = ""

  /** Gestartet vom Timer - ohne Eingaben ausser der Reichweite. */
  case class In(
      @description("Wie viele Tage voraus die Kalender gelesen werden")
      daysAhead: Option[Int],
      @description("A way to override process configuration.\n\n**SHOULD NOT BE USED on Production!**")
      inConfig: Option[InConfig] = None
  ) extends WithConfig[InConfig]:
    lazy val defaultConfig = InConfig()
  end In

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      daysAhead = Some(21)
    )
    lazy val exampleMinimal = example.copy(
      daysAhead = None
    )
  end In

  case class InitIn(
      @description("Wie viele Tage voraus die Kalender gelesen werden")
      daysAhead: Int
  )

  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec

    lazy val example = InitIn(
      daysAhead = 21
    )
    lazy val exampleMinimal = example
  end InitIn

  case class InConfig()

  object InConfig:
    given ApiSchema[InConfig]  = deriveApiSchema
    given InOutCodec[InConfig] = deriveInOutCodec
  end InConfig

  /** Ergebnis des Laufs */
  case class Out(
      @description("Anzahl aktualisierter Berater")
      advisorCount: Int
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      advisorCount = 2
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = process(
    In.example,
    Out.example,
    InitIn.example
  )

  lazy val exampleMinimal = process(
    In.exampleMinimal,
    Out.exampleMinimal,
    InitIn.exampleMinimal
  )
end UpdateAvailability
