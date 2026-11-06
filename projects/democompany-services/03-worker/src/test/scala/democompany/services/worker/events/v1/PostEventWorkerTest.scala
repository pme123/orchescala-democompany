package democompany.services
package worker.events.v1

import democompany.services.domain.events.v1.PostEvent.*

//sbt worker/testOnly *PostEventWorkerTest
class PostEventWorkerTest extends munit.FunSuite:

  lazy val worker = PostEventWorker()

  test("method"):
    assertEquals(worker.method, Method.POST)

  test("apiUri"):
    assertEquals(
      worker.apiUri(In.example).toString,
      s"https://services.democompany.com/api/v1/events"
    )

  test("inputMapper"):
    assertEquals(
      worker.inputMapper(In.example),
      Some(ServiceIn.example)
    )

end PostEventWorkerTest
