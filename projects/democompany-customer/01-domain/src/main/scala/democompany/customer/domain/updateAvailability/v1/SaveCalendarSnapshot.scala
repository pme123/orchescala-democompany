package democompany.customer.domain.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.schema.*

/** Speichert die belegten Zeiten des Beraters (ersetzt den letzten Stand). */
object SaveCalendarSnapshot extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-updateAvailabilityV1-SaveCalendarSnapshot"
  val descr: String = "Speichert die belegten Zeiten des Beraters (ersetzt den letzten Stand)."

  /** Speichert die belegten Zeiten des Beraters (ersetzt den letzten Stand). */
  case class In(
      @description("Berater")
      advisor: Advisor,
      @description("Belegte Zeiten")
      busy: Seq[BusyPeriod]
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      advisor = Advisor.example,
      busy = Seq(BusyPeriod.example)
    )
    lazy val exampleMinimal = example
  end In

  /** Speichert die belegten Zeiten des Beraters (ersetzt den letzten Stand). */
  case class Out(
      @description("Stand")
      savedAt: LocalDateTime
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      savedAt = LocalDateTime.now()
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end SaveCalendarSnapshot
