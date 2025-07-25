package democompany.cards
package worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*

class OrderCreditcardWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  given InOutCodec[InConfig] = InConfig.given_InOutCodec_InConfig

  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    InitIn() 
  
end OrderCreditcardWorker