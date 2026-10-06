package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Terminregeln: Dauer, Vorlaufzeit, Zeitfenster, Puffer, Berater-Pool und ob der Berater bestätigt. */
object AppointmentRulesDmn extends CompanyBpmnDecisionDsl:

  val decisionId = "democompany-customer-bookAppointmentV1-AppointmentRules"
  val descr: String = "Terminregeln: Dauer, Vorlaufzeit, Zeitfenster, Puffer, Berater-Pool und ob der Berater bestätigt."

  /** Eingaben der Terminregeln */
  case class In(
      @description("Thema (advice, mortgage, pension, investment)")
      topic: String,
      @description("Kanal (branch, video, phone)")
      channel: String,
      @description("Kundenstatus (customer, prospect)")
      customerStatus: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      topic = "mortgage",
      channel = "branch",
      customerStatus = "customer"
    )
    lazy val exampleMinimal = example
  end In

  /** Ergebnis der Terminregeln */
  case class Out(
      @description("Dauer in Minuten")
      durationMinutes: Int,
      @description("Vorlaufzeit in Stunden")
      leadTimeHours: Int,
      @description("Zeitfenster von (HH:mm)")
      windowStart: String,
      @description("Zeitfenster bis (HH:mm)")
      windowEnd: String,
      @description("Puffer zwischen Terminen in Minuten")
      bufferMinutes: Int,
      @description("Berater-Pool")
      advisorPool: String,
      @description("Bestätigung durch den Berater nötig")
      confirmationRequired: Boolean
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      durationMinutes = 90,
      leadTimeHours = 48,
      windowStart = "08:00",
      windowEnd = "17:00",
      bufferMinutes = 15,
      advisorPool = "mortgage",
      confirmationRequired = true
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = singleResult(
    In.example,
    Out.example
  )
end AppointmentRulesDmn
