package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Löscht die Reservierung - der Termin ist wieder frei. */
object ReleaseReservation extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-ReleaseReservation"
  val descr: String = "Löscht die Reservierung - der Termin ist wieder frei."

  /** Löscht die Reservierung - der Termin ist wieder frei. */
  case class In(
      @description("Reservierung")
      reservationId: String,
      @description("Das Geheimnis der Reservierung (aus reserveSlot) - nur die eigene wird geändert")
      token: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      reservationId = "anna.berater-2026-10-20T09:00",
      token = "0b1c9a4e-7a43-4f0e-9d39-3a3f6c2d8e11"
    )
    lazy val exampleMinimal = example
  end In

  /** Löscht die Reservierung - der Termin ist wieder frei. */
  case class Out(
      @description("Zeitpunkt der Freigabe")
      releasedAt: LocalDateTime,
      @description("Termin nicht gebucht - das Ergebnis des Prozesses")
      booked: Boolean = false
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
