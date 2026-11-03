package democompany.patterns
package worker.uniqueExecution.v1

import democompany.patterns.domain.uniqueExecution.v1.UniqueExecution.*
import democompany.patterns.worker.uniqueExecution.v1.UniqueExecutionWorker

//sbt worker/testOnly *UniqueExecutionWorkerTest
class UniqueExecutionWorkerTest extends munit.FunSuite:

  lazy val worker = UniqueExecutionWorker()


  test("customInit"):
    val in = In.example
    val out = ???
    assertEquals(
      worker.customInit(in),
      out
    )
  test("customInit minimal"):
    val in = In.exampleMinimal
    val out = ???
    assertEquals(
      worker.customInit(in),
      out
  )



end UniqueExecutionWorkerTest