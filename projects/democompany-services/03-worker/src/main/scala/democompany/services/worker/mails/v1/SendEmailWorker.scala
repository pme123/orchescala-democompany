package democompany.services
package worker.mails.v1

import democompany.services.domain.mails.v1.SendEmail.*

import java.util.UUID

class SendEmailWorker extends CompanyServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]:


  lazy val serviceTask = example

  override lazy val method = Method.PUT

  def apiUri(in: In) = uri"$servicePath/mail"

  override def inputHeaders(in: In) =
    Map("Request-ID" -> UUID.randomUUID().toString)

  override def inputMapper(in: In): Option[ServiceIn] =
    Some(in.copy(body = in.body.replaceAll("\n", "</br>")))


end SendEmailWorker