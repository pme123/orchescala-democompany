package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.LoadAdvisors.*
import democompany.customer.worker.appointments.Advisors

//sbt worker/testOnly *LoadAdvisorsWorkerTest
class LoadAdvisorsWorkerTest extends munit.FunSuite:

  test("the advisors of the configuration - without ADVISORS the demo ones"):
    assume(sys.env.get("ADVISORS").isEmpty, "ADVISORS is set")
    assertEquals(LoadAdvisorsWorker().runWork(In(None)), Right(Out(Advisors.demo, 2)))

end LoadAdvisorsWorkerTest
