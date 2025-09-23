package democompany.cards
package domain.orderCreditcard.v1

object ReceiveEmailME extends CompanyBpmnMessageEventDsl:

  val messageName   =
    "democompany-cards-orderCreditcardV1.ReceiveEmail"
  val descr: String = "Mail Service calls us after response is received."

  case class In(
      sender: String,
      receiver: String,
      subject: String,
      body: String
  )
  object In:
    given ApiSchema[In]     = deriveApiSchema
    given InOutCodec[In]    = deriveInOutCodec
    lazy val example        = In(
      sender = "peter.pan@demoland.com",
      receiver = "customerservice@democompany.ch",
      subject = "RE: Missing Information for ordering creditcard.",
      body =
        """Hello
          |
          |Here the missing information:
          | Birthday: 01.01.1990
          | Gender: Male
          |""".stripMargin
    )
    lazy val exampleMinimal = example
  end In

  lazy val example = messageEvent(In.example)
end ReceiveEmailME
