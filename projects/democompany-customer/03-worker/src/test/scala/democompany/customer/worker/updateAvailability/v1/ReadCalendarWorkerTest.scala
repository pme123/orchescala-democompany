package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.ReadCalendar.*
import democompany.customer.worker.updateAvailability.v1.ReadCalendarWorker

//sbt worker/testOnly *ReadCalendarWorkerTest
class ReadCalendarWorkerTest extends munit.FunSuite:

  lazy val worker = ReadCalendarWorker()


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



end ReadCalendarWorkerTest