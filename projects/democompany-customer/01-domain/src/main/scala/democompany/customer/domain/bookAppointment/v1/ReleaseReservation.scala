package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Löscht die Reservierung - der Termin ist wieder frei. */
object ReleaseReservation extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-ReleaseReservation"
  val descr: String = "Löscht die Reservierung - der Termin ist wieder frei."

  /** Löscht die Reservierung - der Termin ist wieder frei. */
  case class In(
      @description("Reservierung")
      reservationId: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      reservationId = "anna.berater-2026-10-20T09:00"
    )
    lazy val exampleMinimal = example
  end In

  /** Löscht die Reservierung - der Termin ist wieder frei. */
  case class Out(
      @description("Zeitpunkt der Freigabe")
      releasedAt: LocalDateTime
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      releasedAt = LocalDateTime.now()
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end ReleaseReservation
