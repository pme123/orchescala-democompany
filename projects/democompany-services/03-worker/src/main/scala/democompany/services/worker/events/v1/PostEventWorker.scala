package democompany.services
package worker.events.v1

import democompany.services.domain.events.v1.PostEvent.*

class PostEventWorker extends CompanyServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]:

  lazy val serviceTask = example

  override lazy val method = Method.POST

  def apiUri(in: In) = uri"$servicePath"

  override def inputMapper(in: In): Option[ServiceIn] = Some(in)

end PostEventWorker
