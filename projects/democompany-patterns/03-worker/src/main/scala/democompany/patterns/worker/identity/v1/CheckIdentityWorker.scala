package democompany.patterns
package worker.identity.v1

import democompany.patterns.domain.identity.v1.CheckIdentity.*

/** Stands for any service call with the identity of the process. The BPMN mocks it
  * (`_servicesMocked`) - the service is never called, but the ServiceWorker verifies the signature
  * of `_identityCorrelation` before it mocks (an invalid one fails the job).
  */
class CheckIdentityWorker extends CompanyServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]:

  lazy val serviceTask = example

  override lazy val method = Method.GET

  // never called - mocked in the BPMN
  def apiUri(in: In) = uri"http://localhost/democompany-patterns/identity"

  override def outputMapper(
      out: ServiceResponse[ServiceOut],
      in: In
  ) =
    Right(Out())

end CheckIdentityWorker
