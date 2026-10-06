package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.ReleaseReservation.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations

/** Deletes the reservation - the slot is free again. Already gone is fine (a retry). */
class ReleaseReservationWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      stored <- get(reservations, in.reservationId)
      _      <- ZIO.foreachDiscard(stored)(_ => delete(reservations, in.reservationId))
    yield Out(LocalDateTime.now())

end ReleaseReservationWorker
