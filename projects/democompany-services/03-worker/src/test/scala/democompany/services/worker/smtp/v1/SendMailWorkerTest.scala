package democompany.services
package worker.smtp.v1

//sbt worker/testOnly *SendMailWorkerTest
// It sends mails - its behaviour is checked by the simulation of the projects using it; here: the wiring.
class SendMailWorkerTest extends munit.FunSuite:

  test("the topic of the worker"):
    assertEquals(SendMailWorker().topic, "democompany-services-smtpV1-SendMail")

  test("a \\n in the BPMN text is a line break"):
    assertEquals(SendMailWorker.text("Guten Tag\\n\\nGrüsse"), "Guten Tag\n\nGrüsse")

end SendMailWorkerTest
