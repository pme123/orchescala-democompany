package democompany.customer.domain.bookAppointment.v1.schema


/** Ein Termin bei einem Berater */
case class Appointment(
    @description("Berater (Benutzer-ID)")
    advisorId: String,
    @description("Name des Beraters")
    advisorName: String,
    @description("E-Mail des Beraters")
    advisorEmail: String,
    @description("Beginn")
    start: LocalDateTime,
    @description("Ende")
    end: LocalDateTime,
    @description("Thema")
    topic: Topic,
    @description("Kanal")
    channel: Channel
)

object Appointment:
  given ApiSchema[Appointment]  = deriveApiSchema
  given InOutCodec[Appointment] = deriveInOutCodec

  lazy val example = Appointment(
    advisorId = "anna.berater",
    advisorName = "Anna Berater",
    advisorEmail = "anna.berater@democompany.ch",
    start = LocalDateTime.now(),
    end = LocalDateTime.now(),
    topic = Topic.advice,
    channel = Channel.branch
  )
  lazy val exampleMinimal = example
end Appointment
