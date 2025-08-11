package democompany.cards
package domain.orderCreditcard.v1

import schema.*
import democompany.services.domain.clients.v1.schema.Client

object CheckOrderTask extends CompanyBpmnUserTaskDsl:

  val name          = "democompany-cards-orderCreditcardV1-CheckOrderTask"
  val descr: String = ""

  case class In(
      client: Client,
      creditCardAccount: CardAccount,
      mainCardHolder: Option[CardHolder]
  )
  object In:
    given ApiSchema[In]     = deriveApiSchema
    given InOutCodec[In]    = deriveInOutCodec
    lazy val example        = In(
      client = Client.example,
      creditCardAccount = CardAccount.example,
      mainCardHolder = Some(CardHolder.example)
    )
    lazy val exampleMinimal = example.copy(
      creditCardAccount = CardAccount.exampleMinimal,
      mainCardHolder = None
    )
  end In

  case class Out(
      approved: Boolean,
      approveComment: String
  )
  object Out:
    given ApiSchema[Out]    = deriveApiSchema
    given InOutCodec[Out]   = deriveInOutCodec
    lazy val example        = Out(approved = true, approveComment = "Approved with no issues.")
    lazy val exampleMinimal = example // .copy(..=None)
  end Out

  lazy val example = userTask(
    In.example,
    Out.example
  )

  lazy val exampleMinimal = userTask(
    In.exampleMinimal,
    Out.exampleMinimal
  )
end CheckOrderTask
