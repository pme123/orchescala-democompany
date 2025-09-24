package democompany.cards
package worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.EvalResponse.*
import democompany.cards.worker.orderCreditcard.v1.EvalResponseWorker

//sbt worker/testOnly *EvalResponseWorkerTest
class EvalResponseWorkerTest extends munit.FunSuite:

  lazy val worker = EvalResponseWorker()


  test("runWork Email"):
    val in = In.Email.example
    val out = Right(Out.example)
    assertEquals(
      worker.runWork(in),
      out
    )
  test("runWork Telephone"):
    val in = In.Telephone.example
    val out = Right(Out(orderCorrect = true))
    assertEquals(
      worker.runWork(in),
      out
    )
  test("runWork None"):
    val in = In.None.exampleMinimal
    val out = Right(Out(orderCorrect = false))
    assertEquals(
      worker.runWork(in),
      out
    )



end EvalResponseWorkerTest