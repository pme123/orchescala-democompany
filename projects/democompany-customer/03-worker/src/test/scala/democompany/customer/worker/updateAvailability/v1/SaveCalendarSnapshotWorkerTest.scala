package democompany.customer
package worker.updateAvailability.v1

//sbt worker/testOnly *SaveCalendarSnapshotWorkerTest
// It uses the store - its behaviour is checked by the simulation; here: the wiring.
class SaveCalendarSnapshotWorkerTest extends munit.FunSuite:

  test("the topic of the worker is the one in the BPMN"):
    val bpmn = os.read(os.pwd / "src" / "main" / "resources" / "camunda" / "customer-updateAvailabilityV1.bpmn")
    assertEquals(SaveCalendarSnapshotWorker().topic, "democompany-customer-updateAvailabilityV1-SaveCalendarSnapshot")
    assert(bpmn.contains(s"""camunda:topic="${SaveCalendarSnapshotWorker().topic}""""))

end SaveCalendarSnapshotWorkerTest
