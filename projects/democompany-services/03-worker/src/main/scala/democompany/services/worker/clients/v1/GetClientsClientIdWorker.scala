package democompany.services
package worker.clients.v1

import democompany.services.domain.clients.v1.GetClientsClientId.*

class GetClientsClientIdWorker extends CompanyServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]:


  lazy val serviceTask = example

  override lazy val method = Method.GET

  def apiUri(in: In) = uri"$servicePath/${in.clientId}"

  override def outputMapper(
      out: ServiceResponse[ServiceOut],
      in: In
  ) = ???



end GetClientsClientIdWorker