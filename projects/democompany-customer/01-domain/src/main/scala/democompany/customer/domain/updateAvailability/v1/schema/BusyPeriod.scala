package democompany.customer.domain.updateAvailability.v1.schema


/** Eine belegte Zeit im Kalender */
case class BusyPeriod(
    @description("Beginn")
    start: LocalDateTime,
    @description("Ende")
    end: LocalDateTime
)

object BusyPeriod:
  given ApiSchema[BusyPeriod]  = deriveApiSchema
  given InOutCodec[BusyPeriod] = deriveInOutCodec

  lazy val example = BusyPeriod(
    start = LocalDateTime.now(),
    end = LocalDateTime.now()
  )
  lazy val exampleMinimal = example
end BusyPeriod
