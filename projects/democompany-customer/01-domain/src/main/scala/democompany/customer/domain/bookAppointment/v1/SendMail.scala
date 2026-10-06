package democompany.customer.domain.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.schema.*

/** Schickt eine Mail über den SMTP-Server der Umgebung (lokal Mailpit). */
object SendMail extends CompanyBpmnCustomTaskDsl:

  val topicName = "democompany-customer-bookAppointmentV1-SendMail"
  val descr: String = "Schickt eine Mail über den SMTP-Server der Umgebung (lokal Mailpit)."

  /** Schickt eine Mail über den SMTP-Server der Umgebung (lokal Mailpit). */
  case class In(
      @description("Empfänger")
      to: String,
      @description("Betreff")
      subject: String,
      @description("Text - `\\n` ist ein Zeilenumbruch")
      body: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      to = "peter.muster@example.ch",
      subject = "Bitte bestätigen Sie Ihre Terminanfrage",
      body = "Guten Tag"
    )
    lazy val exampleMinimal = example
  end In

  /** Schickt eine Mail über den SMTP-Server der Umgebung (lokal Mailpit). */
  case class Out(
      @description("Zeitpunkt des Versands")
      sentAt: LocalDateTime
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      sentAt = LocalDateTime.now()
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end SendMail
