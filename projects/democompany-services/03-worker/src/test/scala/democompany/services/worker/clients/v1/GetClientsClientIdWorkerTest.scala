package democompany.services
package worker.clients.v1

import democompany.services.domain.clients.v1.*
import democompany.services.domain.clients.v1.GetClientsClientId.*
import democompany.services.worker.clients.v1.GetClientsClientIdWorker

//sbt worker/testOnly *GetClientsClientIdWorkerTest
class GetClientsClientIdWorkerTest extends munit.FunSuite:

  lazy val worker = GetClientsClientIdWorker()


  test("apiUri"):
    assertEquals(
      worker.apiUri(In.example).toString,
      s"https://services.democompany.com/api/v1/clients/$defaultClientId"
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