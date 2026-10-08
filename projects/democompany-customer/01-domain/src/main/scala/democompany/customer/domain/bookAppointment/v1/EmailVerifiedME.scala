package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Der Kunde hat den Link in der Opt-in-Mail angeklickt. */
object EmailVerifiedME extends CompanyBpmnMessageEventDsl:

  val messageName = "democompany-customer-bookAppointmentV1-emailVerified"
  val descr: String = "Der Kunde hat den Link in der Opt-in-Mail angeklickt."

  /** Der Kunde hat den Link in der Opt-in-Mail angeklickt. */
  case class In(
      @description("Zeitpunkt des Klicks")
      verifiedAt: Option[LocalDateTime]
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      verifiedAt = Some(LocalDateTime.now())
    )
    lazy val exampleMinimal = example.copy(
      verifiedAt = None
    )
  end In

  lazy val example = messageEvent(
    In.example
  )
end EmailVerifiedME
