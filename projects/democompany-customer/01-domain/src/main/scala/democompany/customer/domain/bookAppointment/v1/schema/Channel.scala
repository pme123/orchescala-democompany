package democompany.customer.domain.bookAppointment.v1.schema


/** Wie der Termin stattfindet */
enum Channel:
  case branch, video, phone

object Channel:
  given ApiSchema[Channel]  = deriveEnumApiSchema
  given InOutCodec[Channel] = deriveEnumInOutCodec

  lazy val example = Channel.branch
end Channel
