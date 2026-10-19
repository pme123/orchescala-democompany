package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.ReleaseReservation.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations
import democompany.customer.worker.appointments.Reservations

/** Deletes the reservation - the slot is free again. Already gone is fine (a retry), and so is a
  * reservation of another request (it took the slot over) - that one stays.
  */
class ReleaseReservationWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      stored <- get(reservations, in.reservationId)
      _      <- ZIO.foreachDiscard(stored.filter(s => Reservations.releasable(s.entity, in.token)))(_ =>
                  delete(reservations, in.reservationId)
                )
    yield Out(LocalDateTime.now(), booked = false)

end ReleaseReservationWorker
