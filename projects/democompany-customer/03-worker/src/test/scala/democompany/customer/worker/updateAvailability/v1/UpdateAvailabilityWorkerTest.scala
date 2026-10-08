package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.UpdateAvailability.*
import democompany.customer.worker.updateAvailability.v1.UpdateAvailabilityWorker

//sbt worker/testOnly *UpdateAvailabilityWorkerTest
class UpdateAvailabilityWorkerTest extends munit.FunSuite:

  lazy val worker = UpdateAvailabilityWorker()


  test("customInit"):
    val in = In.example
    val out = InitIn(
      daysAhead = in.daysAhead.getOrElse(21)
    )
    assertEquals(
      worker.customInit(in),
      out
    )
  test("customInit minimal"):
    val in = In.exampleMinimal
    val out = InitIn(
      daysAhead = in.daysAhead.getOrElse(21)
    )
    assertEquals(
      worker.customInit(in),
      out
  )



end UpdateAvailabilityWorkerTest