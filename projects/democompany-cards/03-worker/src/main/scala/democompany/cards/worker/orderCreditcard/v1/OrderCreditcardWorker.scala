package democompany.cards
package worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*

class OrderCreditcardWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  given InOutCodec[InConfig] = InConfig.given_InOutCodec_InConfig

  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    InitIn(
      initCreditCardAccount = in.creditCardAccount.copy(accountId = None),
      simpleValue = if in.clientId > 100 then Some("Hello World") else None
    )
 
 
  
end OrderCreditcardWorker