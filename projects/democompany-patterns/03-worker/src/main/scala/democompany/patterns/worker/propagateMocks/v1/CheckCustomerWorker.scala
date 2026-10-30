package democompany.patterns
package worker.propagateMocks.v1

import democompany.patterns.domain.propagateMocks.v1.CheckCustomer.*

/** The service of «Kunde prüfen» does not exist (nothing listens on port 1) - the job only
  * succeeds if `_servicesMocked` reached the sub process. Before that, the ServiceWorker verifies
  * the signature of `_identityCorrelation` (an invalid or missing one fails the job).
  */
class CheckCustomerWorker extends CompanyServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]:

  lazy val serviceTask = example

  override lazy val method = Method.GET

  def apiUri(in: In) = uri"http://localhost:1/democompany-patterns/customer"

  override def outputMapper(
      out: ServiceResponse[ServiceOut],
      in: In
  ) =
    Right(Out())

end CheckCustomerWorker
