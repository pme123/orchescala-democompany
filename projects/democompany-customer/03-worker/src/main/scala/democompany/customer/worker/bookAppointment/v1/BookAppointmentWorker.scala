package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*

class BookAppointmentWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:

  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    ??? //TODO add variable initialisation (to simplify the process expressions) or remove function
    // NoInput() // if no initialization is needed
  
end BookAppointmentWorker