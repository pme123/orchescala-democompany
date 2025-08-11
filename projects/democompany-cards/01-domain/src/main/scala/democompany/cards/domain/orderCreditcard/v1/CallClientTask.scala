package democompany.cards
package domain.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.schema.{CardAccount, CardHolder}
import democompany.services.domain.clients.v1.schema.Client

object CallClientTask extends CompanyBpmnUserTaskDsl:

  val name = "CallClientTask"
  val descr: String = "The Customer Service must call the client due to missing information."


  case class In(
    client: Client,
    approveComment: String,
    creditCardAccount: CardAccount,
    mainCardHolder: Option[CardHolder]

  )
  object In:
    given ApiSchema[In] = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example = In(
      client = Client.example,
      approveComment = "Approved with no issues.",
      creditCardAccount = CardAccount.example,
      mainCardHolder = Some(CardHolder.example)
    )
    lazy val exampleMinimal = example.copy(
      creditCardAccount = CardAccount.exampleMinimal,
      mainCardHolder = None
    )


  case class Out(
      approvedOnTelephone: Boolean,
      approveOnTelephoneComment: String
  )
  object Out:
    given ApiSchema[Out] = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example = Out(
      approvedOnTelephone = true,
      approveOnTelephoneComment = "Approved on telephone. Everything could be clarified."
    )
    lazy val exampleMinimal = example //.copy(..=None)



  lazy val example = userTask(
    In.example,
    Out.example
    
  )

  lazy val exampleMinimal = userTask(
    In.exampleMinimal,
    Out.exampleMinimal
    
  )
end CallClientTask