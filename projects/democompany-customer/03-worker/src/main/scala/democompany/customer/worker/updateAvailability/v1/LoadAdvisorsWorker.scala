package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.LoadAdvisors.*
import democompany.customer.worker.appointments.Advisors

/** The advisors of the configuration (see `Advisors`) - later from the IdP by their role. */
class LoadAdvisorsWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    Right(Out(Advisors.all, Advisors.all.size))

end LoadAdvisorsWorker
