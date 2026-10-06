package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.AssignCustomer.*
import democompany.customer.worker.bookAppointment.v1.AssignCustomerWorker

//sbt worker/testOnly *AssignCustomerWorkerTest
class AssignCustomerWorkerTest extends munit.FunSuite:

  lazy val worker = AssignCustomerWorker()


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



end AssignCustomerWorkerTest