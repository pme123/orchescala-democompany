package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Der Berater nimmt den Termin an oder lehnt ihn mit Begründung ab. */
object ConfirmAppointmentUT extends CompanyBpmnUserTaskDsl:

  val name = "ConfirmAppointmentTask"
  val descr: String = "Der Berater nimmt den Termin an oder lehnt ihn mit Begründung ab."

  /** Der Berater nimmt den Termin an oder lehnt ihn mit Begründung ab. */
  case class In(
      @description("Der reservierte Termin")
      appointment: Appointment,
      @description("Kontaktdaten")
      contact: Contact,
      @description("Kunde oder Interessent")
      customerStatus: CustomerStatus,
      @description("Bemerkung des Kunden")
      remark: Option[String]
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      appointment = Appointment.example,
      contact = Contact.example,
      customerStatus = CustomerStatus.customer,
      remark = Some("Beispiel")
    )
    lazy val exampleMinimal = example.copy(
      remark = None
    )
  end In

  /** Der Berater nimmt den Termin an oder lehnt ihn mit Begründung ab. */
  case class Out(
      @description("Termin angenommen")
      accepted: Boolean,
      @description("Begründung - geht in die Absage an den Kunden")
      declineReason: Option[String]
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      accepted = true,
      declineReason = Some("Beispiel")
    )
    lazy val exampleMinimal = example.copy(
      declineReason = None
    )
  end Out

  lazy val example = userTask(
    In.example,
    Out.example
  )
end ConfirmAppointmentUT
