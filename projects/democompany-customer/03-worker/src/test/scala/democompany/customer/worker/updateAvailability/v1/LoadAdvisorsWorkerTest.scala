package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.LoadAdvisors.*
import democompany.customer.worker.updateAvailability.v1.LoadAdvisorsWorker

//sbt worker/testOnly *LoadAdvisorsWorkerTest
class LoadAdvisorsWorkerTest extends munit.FunSuite:

  lazy val worker = LoadAdvisorsWorker()


  test("runWork"):
    val in = In.example
    val out = Right(Out.example)
    assertEquals(
      worker.runWork(in),
      out
    )
  test("runWork minimal"):
    val in = In.exampleMinimal
    val out = Right(Out.exampleMinimal)
    assertEquals(
      worker.runWork(in),
      out
    )



end LoadAdvisorsWorkerTest