package democompany.patterns
package domain.identity.v1

/** The user task of the identity example - who completes it is the actor the process takes over. */
object ApproveRequestUT extends CompanyBpmnUserTaskDsl:

  val name          = "ApproveRequestUT"
  val descr: String =
    "Antrag freigeben - wer die Aufgabe erledigt, steht danach als `_identityCorrelationOfActor` im Prozess."

  case class In(
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = Identity.In.example.customerName)
  end In

  case class Out(
      @description("Antrag freigegeben?")
      approved: Boolean = true
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out()
  end Out

  lazy val example = userTask(
    In.example,
    Out.example
  )
end ApproveRequestUT
