package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.appointments.ReservationStatus
import democompany.customer.domain.bookAppointment.v1.BookReservation.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations

/** The reservation becomes a booking - it no longer expires. */
class BookReservationWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      stored <- getExisting(reservations, in.reservationId)
      _      <- save(reservations, stored.entity.copy(status = ReservationStatus.booked), Some(stored.version))
    yield Out(LocalDateTime.now())

end BookReservationWorker
