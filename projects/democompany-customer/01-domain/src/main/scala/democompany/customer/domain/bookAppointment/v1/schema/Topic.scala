package democompany.customer.domain.bookAppointment.v1.schema


/** Thema des Termins */
enum Topic:
  case advice, mortgage, pension, investment

object Topic:
  given ApiSchema[Topic]  = deriveEnumApiSchema
  given InOutCodec[Topic] = deriveEnumInOutCodec

  lazy val example = Topic.advice
end Topic
