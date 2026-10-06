package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.LoadAdvisors.*

class LoadAdvisorsWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    ???
  end runWork

end LoadAdvisorsWorker