package democompany.services
package domain.clients.v1

object ClientsV1:

  final val serviceVersion = "1.0"
  final val serviceLabel = s"Clients $serviceVersion"

  val description = "The Clients Services"

trait ClientsV1
  extends CompanyBpmnServiceTaskDsl:
  final val serviceLabel = ClientsV1.serviceLabel
  val serviceVersion = ClientsV1.serviceVersion
end ClientsV1
