package democompany.patterns
package worker.propagateMocks.v1

import democompany.patterns.domain.propagateMocks.v1.PropagateMocksSub.*

/** The InitWorker of the sub process - mocked if the caller passed its topic in `_mockedWorkers`.
  */
class PropagateMocksSubWorker extends CompanyInitWorkerDsl[In, Out, NoInput, NoInput]:

  lazy val inOutExample = example

  override def customInit(in: In): NoInput = NoInput()

end PropagateMocksSubWorker
