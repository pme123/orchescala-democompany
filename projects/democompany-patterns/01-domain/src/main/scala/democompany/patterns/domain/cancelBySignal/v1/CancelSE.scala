package democompany.patterns
package domain.cancelBySignal.v1

/** The cancel signal of the example - `cancel-<process instance>`. Orchescala puts the id of the
  * instance in place of `{processInstanceId}` when it sends the signal.
  */
object CancelSE extends CompanyBpmnSignalEventDsl:

  val messageName   = s"cancel-${SignalEvent.Dynamic_ProcessInstance}"
  val descr: String =
    "Bricht die Instanz ab - der Ereignis-Subprozess «Abbruch» fängt das Signal und beendet den Prozess mit `canceled`."

  lazy val example = signalEvent()
end CancelSE
