package democompany.customer.domain.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.schema.*

/** Liest die belegten Zeiten eines Beraters (Kalender-Mock, später Outlook über Graph). */
object ReadCalendar extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-updateAvailabilityV1-ReadCalendar"
  val descr: String = "Liest die belegten Zeiten eines Beraters (Kalender-Mock, später Outlook über Graph)."

  /** Liest die belegten Zeiten eines Beraters (Kalender-Mock, später Outlook über Graph). */
  case class In(
      @description("Berater")
      advisor: Advisor,
      @description("Wie viele Tage voraus")
      daysAhead: Int
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      advisor = Advisor.example,
      daysAhead = 21
    )
    lazy val exampleMinimal = example
  end In

  /** Liest die belegten Zeiten eines Beraters (Kalender-Mock, später Outlook über Graph). */
  case class Out(
      @description("Belegte Zeiten")
      busy: Seq[BusyPeriod]
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      busy = Seq(BusyPeriod.example)
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end ReadCalendar
