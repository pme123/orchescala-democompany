package democompany.patterns.domain
package uniqueExecution.v1

/** Antrag bearbeiten - solange die Aufgabe offen ist, läuft der Prozess; ein zweiter Start landet hier. */
object EditRequestUT extends CompanyBpmnUserTaskDsl:

  val name = "EditRequestUT"
  val descr: String = "Antrag bearbeiten - solange die Aufgabe offen ist, läuft der Prozess; ein zweiter Start landet hier."

  case class In(
      customerName: String
  )

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

    lazy val example = In(
      customerName = UniqueExecution.In.example.customerName
    )
    lazy val exampleMinimal = example
  end In

  case class Out(
      isOk: Boolean
  )

  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

    lazy val example = Out(
      isOk = true
    )
    lazy val exampleMinimal = example
  end Out

  lazy val example = userTask(
    In.example,
    Out.example
  )
end EditRequestUT
