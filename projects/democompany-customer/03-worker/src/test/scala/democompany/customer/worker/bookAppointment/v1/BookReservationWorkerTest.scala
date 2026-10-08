package democompany.customer
package worker.bookAppointment.v1

//sbt worker/testOnly *BookReservationWorkerTest
// It sends mails or uses the store - its behaviour is checked by the simulation; here: the wiring.
class BookReservationWorkerTest extends munit.FunSuite:

  test("the topic of the worker is the one in the BPMN"):
    val bpmn = os.read(os.pwd / "src" / "main" / "resources" / "camunda" / "customer-bookAppointmentV1.bpmn")
    assertEquals(BookReservationWorker().topic, "democompany-customer-bookAppointmentV1-BookReservation")
    assert(bpmn.contains(s"""camunda:topic="${BookReservationWorker().topic}""""))

end BookReservationWorkerTest
