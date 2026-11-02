package democompany.patterns
package worker.stopWithError.v1

import democompany.patterns.domain.stopWithError.v1.StopWithError.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the stop-with-error example - its topic is the process id. */
class StopWithErrorWorker extends CompanyInitWorkerDsl[In, Out, NoInput, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // no variables of its own - the example is about the end that stops the process
  override def customInit(in: In): NoInput = NoInput()

end StopWithErrorWorker
