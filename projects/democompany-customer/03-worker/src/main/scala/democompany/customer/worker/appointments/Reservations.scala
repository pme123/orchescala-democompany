package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}

/** What a slot's reservation means for others, and what a step of the process may do with it -
  * only with its own (the token): a reservation of another request stays as it is.
  */
object Reservations:

  /** Taken for everyone else: booked, requested (the opt-in is done) or reserved and not expired.
    * Only an expired reservation (no opt-in) is free again - `reserveSlot` takes it over.
    */
  def isTaken(r: Reservation, now: LocalDateTime): Boolean =
    r.status != ReservationStatus.reserved || r.reservedUntil.isAfter(now)

  /** After the opt-in: held until the advisor decides - it does not expire any more. */
  def requested(r: Reservation, token: String): Either[String, Reservation] =
    own(r, token).flatMap: r =>
      if r.status == ReservationStatus.booked then Left(s"The reservation ${r.id} is booked already")
      else Right(r.copy(status = ReservationStatus.requested))

  /** Booked - it no longer expires. */
  def booked(r: Reservation, token: String): Either[String, Reservation] =
    own(r, token).map(_.copy(status = ReservationStatus.booked))

  /** Released (deleted) - only the own one: a reservation of another request stays. */
  def releasable(r: Reservation, token: String): Boolean =
    r.token == token

  private def own(r: Reservation, token: String): Either[String, Reservation] =
    if r.token == token then Right(r)
    else Left(s"The reservation ${r.id} belongs to another request")

end Reservations
