package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.ReleaseReservation.*
import democompany.customer.worker.bookAppointment.v1.ReleaseReservationWorker

//sbt worker/testOnly *ReleaseReservationWorkerTest
class ReleaseReservationWorkerTest extends munit.FunSuite:

  lazy val worker = ReleaseReservationWorker()


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



end ReleaseReservationWorkerTest