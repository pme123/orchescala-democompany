package democompany.cards
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "Card Management"

  lazy val projectDescr =
    "Processes and Workers related to Card Management."

  val version = "0.1.0-SNAPSHOT"

  document(
    orderCreditcardApi
  )

  private lazy val orderCreditcardApi =
    import democompany.cards.domain.orderCreditcard.v1.*
    api(OrderCreditcard.example)(
      CheckOrderTask.example,
      CallClientTask.example
    )
end ApiProjectCreator
