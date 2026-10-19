package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookReservation.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations
import democompany.customer.worker.appointments.Reservations

/** The reservation becomes a booking - it no longer expires. Only the own one (the token): a
  * reservation of another request is never booked for this one.
  */
class BookReservationWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      stored <- getExisting(reservations, in.reservationId)
      booked <- ZIO.fromEither(Reservations.booked(stored.entity, in.token)).mapError(WorkerError.CustomError(_))
      _      <- save(reservations, booked, Some(stored.version))
    yield Out(LocalDateTime.now(), booked = true)

end BookReservationWorker
