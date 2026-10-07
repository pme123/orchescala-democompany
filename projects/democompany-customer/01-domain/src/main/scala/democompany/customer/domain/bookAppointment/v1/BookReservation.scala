package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Setzt die Reservierung auf gebucht. */
object BookReservation extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-BookReservation"
  val descr: String = "Setzt die Reservierung auf gebucht."

  /** Setzt die Reservierung auf gebucht. */
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

  /** Setzt die Reservierung auf gebucht. */
  case class Out(
      @description("Zeitpunkt der Buchung")
      bookedAt: LocalDateTime,
      @description("Termin gebucht - das Ergebnis des Prozesses")
      booked: Boolean = true
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      bookedAt = LocalDateTime.now()
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end BookReservation
