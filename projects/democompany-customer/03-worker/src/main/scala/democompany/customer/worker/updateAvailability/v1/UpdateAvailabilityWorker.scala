package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.UpdateAvailability.*

class UpdateAvailabilityWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:

  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    InitIn(
      daysAhead = in.daysAhead.getOrElse(21)
    ) //TODO add variable initialisation (to simplify the process expressions) or remove function
    // NoInput() // if no initialization is needed
  
end UpdateAvailabilityWorker