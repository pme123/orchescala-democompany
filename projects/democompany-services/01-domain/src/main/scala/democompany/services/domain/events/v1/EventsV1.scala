package democompany.services
package domain.events.v1

object EventsV1:

  final val serviceVersion = "1.0"
  final val serviceLabel = s"Events $serviceVersion"

  val description = "Takes the events of the processes - e.g. for a monitoring or another system"

trait EventsV1
  extends CompanyBpmnServiceTaskDsl:
  final val serviceLabel = EventsV1.serviceLabel
  val serviceVersion = EventsV1.serviceVersion
end EventsV1
