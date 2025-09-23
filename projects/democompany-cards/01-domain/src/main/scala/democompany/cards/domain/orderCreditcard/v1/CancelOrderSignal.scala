package democompany.cards
package domain.orderCreditcard.v1

object CancelOrderSignal extends CompanyBpmnSignalEventDsl:

  val messageName = s"democompany-cards-orderCreditcardV1-${SignalEvent.Dynamic_ProcessInstance}.CancelOrderSignal"
  val descr: String = "Signal to cancel the order."

  type In = NoInput

  lazy val example = signalEvent(NoInput())
end CancelOrderSignal