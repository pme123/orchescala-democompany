package democompany.services
package domain.mails.v1

object MailsV1:

  final val serviceVersion = "1.0"
  final val serviceLabel = s"Mails $serviceVersion"

  val description = "Wraps the mail Server"

trait MailsV1
  extends CompanyBpmnServiceTaskDsl:
  final val serviceLabel = MailsV1.serviceLabel
  val serviceVersion = MailsV1.serviceVersion
end MailsV1
