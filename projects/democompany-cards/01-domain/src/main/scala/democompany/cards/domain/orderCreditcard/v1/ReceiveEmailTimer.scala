package democompany.cards
package domain.orderCreditcard.v1

// not supported in Camunda 8
object ReceiveEmailTimer extends CompanyBpmnTimerEventDsl:

  val title = "democompany-cards-orderCreditcardV1.ReceiveEmailTE"
  val descr: String = "Timer to send reminder email."

  lazy val example = timerEvent()
end ReceiveEmailTimer