package democompany.services
package worker.mails.v1

import democompany.services.domain.mails.v1.SendEmail.*
import democompany.services.worker.mails.v1.SendEmailWorker

//sbt worker/testOnly *SendEmailWorkerTest
class SendEmailWorkerTest extends munit.FunSuite:

  lazy val worker = SendEmailWorker()

  test("method"):
    assertEquals(worker.method, Method.PUT)

  test("apiUri"):
    assertEquals(
      worker.apiUri(In.example).toString,
      s"https://services.democompany.com/api/v1/mails/mail"
    )

  test("inputMapper"):
    assertEquals(
      worker.inputMapper(In.example),
      Some(ServiceIn.example)
    )

  test("inputMapper minimal"):
    assertEquals(
      worker.inputMapper(In.exampleMinimal),
      Some(ServiceIn.exampleMinimal)
    )



end SendEmailWorkerTest