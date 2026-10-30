package democompany.patterns
package domain.uniqueExecution.v1

/** The user task of the unique execution example - while it waits, the process is running. */
object EditRequestUT extends CompanyBpmnUserTaskDsl:

  val name          = "EditRequestUT"
  val descr: String =
    "Antrag bearbeiten - solange die Aufgabe offen ist, läuft der Prozess; ein zweiter Start landet hier."

  case class In(
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = UniqueExecution.In.example.customerName)
  end In

  lazy val example = userTask(
    In.example,
    NoOutput()
  )
end EditRequestUT
