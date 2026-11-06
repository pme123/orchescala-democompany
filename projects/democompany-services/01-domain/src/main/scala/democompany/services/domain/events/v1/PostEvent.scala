package democompany.services
package domain.events.v1

/** Reports an event of a process: what happened (`eventKey`), in which process and instance. Used
  * by the event process of the pattern «Prozess-Event senden» (democompany-patterns).
  */
object PostEvent extends EventsV1:

  val topicName     = "democompany-services-eventsV1-PostEvent"
  val descr: String = "Report an event of a process - what happened, in which process and instance"

  val path = "POST: events"

  case class In(
      @description("What happened, e.g. `started`, `finished`.")
      eventKey: String,
      @description("The id of the process that reports the event.")
      processDefinitionKey: String,
      @description("The instance of the process that reports the event.")
      processInstanceId: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      eventKey = "started",
      processDefinitionKey = "democompany-patterns-sendProcessEventV1",
      processInstanceId = "4711"
    )
  end In

  case class Out(
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out()
  end Out

  type ServiceIn  = In       // the event as it is
  type ServiceOut = NoOutput // the service answers 204

  object ServiceIn:
    lazy val example = In.example

  object ServiceOut:
    lazy val example = NoOutput()
    lazy val mock    = MockedServiceResponse.success204
  end ServiceOut

  lazy val example = serviceTask(
    In.example,
    Out.example,
    ServiceOut.mock,
    ServiceIn.example
  )
end PostEvent
