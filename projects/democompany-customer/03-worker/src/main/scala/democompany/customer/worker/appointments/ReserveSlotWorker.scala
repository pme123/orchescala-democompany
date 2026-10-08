package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.appointments.ReserveSlot.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations

/** Reserves the slot for 30 minutes (as long as the opt-in may take, `timerVerifyEmail`). The id
  * `<advisor>-<start>` makes a slot exist once: is it reserved or booked, the answer is a 409
  * «leider vergeben». An expired reservation that was never booked is taken over. The token (a
  * random UUID) is the business key of the process and the secret of the links in the mails.
  */
class ReserveSlotWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    val now   = LocalDateTime.now()
    val a     = in.appointment
    val id    = Reservation.idOf(a.advisorId, a.start)
    val until = now.plusMinutes(ReserveSlotWorker.reservedMinutes)
    // unguessable - the business key of the process and the secret of the links in the mails
    val token = java.util.UUID.randomUUID().toString
    // 409 / 400: the page shows its own text for them (the public gateway passes the status only)
    val taken = WorkerError.CustomError.refused(409, s"Der Termin ${AppointmentTexts.of(a)} ist leider vergeben - bitte einen anderen wählen.")
    def invalid(why: String) = WorkerError.CustomError.refused(400, why)
    for
      _        <- ZIO.when(Advisors.find(a.advisorId).isEmpty)(ZIO.fail(invalid(s"Unknown advisor ${a.advisorId}")))
      _        <- ZIO.when(!a.start.isAfter(now))(ZIO.fail(invalid(s"The slot ${a.start} is in the past")))
      existing <- get(reservations, id)
      version  <- existing match
                    case None                                                                => ZIO.none
                    case Some(s) if s.entity.status == ReservationStatus.reserved && s.entity.reservedUntil.isBefore(now) =>
                      ZIO.some(s.version)
                    case Some(_)                                                             => ZIO.fail(taken)
      _        <- save(
                    reservations,
                    Reservation(id, token, a, in.contact, in.remark, ReservationStatus.reserved, until),
                    version
                  ).mapError(e => if e.errorMsg.toLowerCase.contains("version") then taken else e)
    yield Out(id, token, until)
    end for
  end runWorkZIO

end ReserveSlotWorker

object ReserveSlotWorker:
  val reservedMinutes: Long = 30
