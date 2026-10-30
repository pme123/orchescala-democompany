package democompany.patterns
package domain.uniqueExecution.v1

/** The start message of the unique execution example sent a second time - with the `businessKey`
  * of the running instance. The event sub process «Prozess läuft schon» catches it.
  */
object StartAgainME extends CompanyBpmnMessageEventDsl:

  val messageName   = UniqueExecution.processName
  val descr: String =
    "Dieselbe Start-Nachricht ein zweites Mal - mit dem `businessKey` der laufenden Instanz."

  lazy val example = messageEvent(UniqueExecution.In.example)
end StartAgainME
