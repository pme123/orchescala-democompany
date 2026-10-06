package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookReservation.*

class BookReservationWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    ???
  end runWork

end BookReservationWorker