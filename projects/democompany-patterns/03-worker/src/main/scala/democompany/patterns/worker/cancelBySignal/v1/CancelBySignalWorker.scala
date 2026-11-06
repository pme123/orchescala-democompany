package democompany.patterns
package worker.cancelBySignal.v1

import democompany.patterns.domain.cancelBySignal.v1.CancelBySignal.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the cancel by signal example - its topic is the process id. */
class CancelBySignalWorker extends CompanyInitWorkerDsl[In, Out, InitIn, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // `waitForSignal = true` - from now on the cancel signal may come
  override def customInit(in: In): InitIn = InitIn()

end CancelBySignalWorker
