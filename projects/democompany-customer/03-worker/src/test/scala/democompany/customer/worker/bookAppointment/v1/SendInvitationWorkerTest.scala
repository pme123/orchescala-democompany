package democompany.customer
package worker.bookAppointment.v1

//sbt worker/testOnly *SendInvitationWorkerTest
// It sends mails or uses the store - its behaviour is checked by the simulation; here: the wiring.
class SendInvitationWorkerTest extends munit.FunSuite:

  test("the topic of the worker is the one in the BPMN"):
    val bpmn = os.read(os.pwd / "src" / "main" / "resources" / "camunda" / "customer-bookAppointmentV1.bpmn")
    assertEquals(SendInvitationWorker().topic, "democompany-customer-bookAppointmentV1-SendInvitation")
    assert(bpmn.contains(s"""camunda:topic="${SendInvitationWorker().topic}""""))

end SendInvitationWorkerTest
