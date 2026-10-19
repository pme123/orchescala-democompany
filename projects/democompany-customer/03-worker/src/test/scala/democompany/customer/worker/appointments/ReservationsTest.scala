package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Contact}

//sbt worker/testOnly *ReservationsTest
class ReservationsTest extends munit.FunSuite:

  private val now     = LocalDateTime.of(2026, 10, 19, 12, 0)
  private val expired = now.minusMinutes(1)
  private def reservation(status: ReservationStatus, until: LocalDateTime = expired) =
    Reservation("anna.berater-2026-10-22T08:30", "own", Appointment.example, Contact.example, None, status, until)

  test("taken for others: reserved before the end of the opt-in, requested, booked"):
    assert(Reservations.isTaken(reservation(ReservationStatus.reserved, now.plusMinutes(10)), now))
    assert(Reservations.isTaken(reservation(ReservationStatus.requested), now))
    assert(Reservations.isTaken(reservation(ReservationStatus.booked), now))

  test("free again only without opt-in after its 30 minutes"):
    assert(!Reservations.isTaken(reservation(ReservationStatus.reserved), now))

  test("after the opt-in the own reservation is requested - it does not expire"):
    val requested = Reservations.requested(reservation(ReservationStatus.reserved), "own")
    assertEquals(requested.map(_.status), Right(ReservationStatus.requested))
    assert(requested.exists(Reservations.isTaken(_, now.plusDays(2))))

  test("a reservation of another request is not changed - not requested, not booked, not released"):
    val other = reservation(ReservationStatus.reserved)
    assert(Reservations.requested(other, "someone else").isLeft)
    assert(Reservations.booked(other, "someone else").isLeft)
    assert(!Reservations.releasable(other, "someone else"))

  test("a booked one is not requested again"):
    assert(Reservations.requested(reservation(ReservationStatus.booked), "own").isLeft)

  test("the own one is booked and released"):
    assertEquals(Reservations.booked(reservation(ReservationStatus.requested), "own").map(_.status), Right(ReservationStatus.booked))
    assert(Reservations.releasable(reservation(ReservationStatus.requested), "own"))

end ReservationsTest
