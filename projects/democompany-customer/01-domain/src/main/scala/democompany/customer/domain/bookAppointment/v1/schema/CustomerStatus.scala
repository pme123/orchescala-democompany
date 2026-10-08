package democompany.customer.domain.bookAppointment.v1.schema


/** Kunde der Bank oder Interessent */
enum CustomerStatus:
  case customer, prospect

object CustomerStatus:
  given ApiSchema[CustomerStatus]  = deriveEnumApiSchema
  given InOutCodec[CustomerStatus] = deriveEnumInOutCodec

  lazy val example = CustomerStatus.customer
end CustomerStatus
