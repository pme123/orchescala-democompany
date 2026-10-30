package democompany.patterns
package worker.propagateMocks.v1

import democompany.patterns.domain.propagateMocks.v1.PropagateMocks.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the calling process - its topic is the process id. */
class PropagateMocksWorker extends CompanyInitWorkerDsl[In, Out, NoInput, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // no variables of its own - the example is about what reaches the sub process
  override def customInit(in: In): NoInput = NoInput()

end PropagateMocksWorker
