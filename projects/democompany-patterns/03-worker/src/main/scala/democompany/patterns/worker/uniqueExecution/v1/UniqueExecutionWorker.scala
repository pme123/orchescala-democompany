package democompany.patterns
package worker.uniqueExecution.v1

import democompany.patterns.domain.uniqueExecution.v1.UniqueExecution.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the unique execution example - its topic is the process id. */
class UniqueExecutionWorker extends CompanyInitWorkerDsl[In, Out, InitIn, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // `alreadyRunning` is false at the start - the event sub process sets it to true
  override def customInit(in: In): InitIn = InitIn()

end UniqueExecutionWorker
