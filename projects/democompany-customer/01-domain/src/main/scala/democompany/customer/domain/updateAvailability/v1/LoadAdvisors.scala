package democompany.customer.domain.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.schema.*

/** Lädt die Berater mit der Rolle kundenberater. */
object LoadAdvisors extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-updateAvailabilityV1-LoadAdvisors"
  val descr: String = "Lädt die Berater mit der Rolle kundenberater."

  /** Lädt die Berater mit der Rolle kundenberater. */
  case class In(
      @description("Rolle der Berater im IdP - ohne Angabe kundenberater")
      role: Option[String]
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      role = Some("kundenberater")
    )
    lazy val exampleMinimal = example.copy(
      role = None
    )
  end In

  /** Lädt die Berater mit der Rolle kundenberater. */
  case class Out(
      @description("Die Berater")
      advisors: Seq[Advisor],
      @description("Wie viele - das Ergebnis des Prozesses")
      advisorCount: Int
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      advisors = Seq(Advisor.example),
      advisorCount = 1
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end LoadAdvisors
