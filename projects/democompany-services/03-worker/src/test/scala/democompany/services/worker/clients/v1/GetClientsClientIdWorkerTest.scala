package democompany.services
package worker.clients.v1

import democompany.services.domain.clients.v1.GetClientsClientId.*
import democompany.services.worker.clients.v1.GetClientsClientIdWorker

//sbt worker/testOnly *GetClientsClientIdWorkerTest
class GetClientsClientIdWorkerTest extends munit.FunSuite:

  lazy val worker = GetClientsClientIdWorker()


  test("apiUri"):
    assertEquals(
      worker.apiUri(In.example).toString,
      s"NOT-SET/YourPath"
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

  test("outputMapper"):
    assertEquals(
      worker.outputMapper(
        ServiceOut.mock.toServiceResponse,
        In.example
      ),
      Right(Out.example)
    )
  test("outputMapper minimal"):
    assertEquals(
      worker.outputMapper(
        ServiceOut.mockMinimal.toServiceResponse,
        In.exampleMinimal
      ),
      Right(Out.exampleMinimal)
    )




end GetClientsClientIdWorkerTest