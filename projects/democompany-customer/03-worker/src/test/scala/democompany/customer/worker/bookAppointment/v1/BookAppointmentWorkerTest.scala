package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
import democompany.customer.worker.bookAppointment.v1.BookAppointmentWorker

//sbt worker/testOnly *BookAppointmentWorkerTest
class BookAppointmentWorkerTest extends munit.FunSuite:

  lazy val worker = BookAppointmentWorker()


  test("customInit"):
    val in = In.example
    val out = ???
    assertEquals(
      worker.customInit(in),
      out
    )
  test("customInit minimal"):
    val in = In.exampleMinimal
    val out = ???
    assertEquals(
      worker.customInit(in),
      out
  )



end BookAppointmentWorkerTest