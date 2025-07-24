package democompany.cards
package worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import democompany.cards.worker.orderCreditcard.v1.OrderCreditcardWorker

//sbt worker/testOnly *OrderCreditcardWorkerTest
class OrderCreditcardWorkerTest extends munit.FunSuite:

  lazy val worker = OrderCreditcardWorker()


  test("customInit"):
    val in = inExample
    val out = InitIn()
    assertEquals(
      worker.customInit(in),
      out
    )
  test("customInit minimal"):
    val in = inExampleMinimal
    val out = InitIn()
    assertEquals(
      worker.customInit(in),
      out
  )


end OrderCreditcardWorkerTest