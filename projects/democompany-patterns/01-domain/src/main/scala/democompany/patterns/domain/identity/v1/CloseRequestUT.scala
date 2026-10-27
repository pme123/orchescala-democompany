package democompany.patterns
package domain.identity.v1

/** The user task between «Identität übernehmen» and «Identität zurücksetzen» - here the process
  * acts with the identity of the person who approved the request.
  */
object CloseRequestUT extends CompanyBpmnUserTaskDsl:

  val name          = "CloseRequestUT"
  val descr: String =
    "Antrag abschliessen - der Prozess hat hier die Identität der Person, die den Antrag freigegeben hat."

  case class In(
      customerName: String,
      approved: Boolean
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      customerName = Identity.In.example.customerName,
      approved = ApproveRequestUT.Out.example.approved
    )
  end In

  case class Out(
      @description("Antrag abgeschlossen?")
      closed: Boolean = true
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
end CloseRequestUT
