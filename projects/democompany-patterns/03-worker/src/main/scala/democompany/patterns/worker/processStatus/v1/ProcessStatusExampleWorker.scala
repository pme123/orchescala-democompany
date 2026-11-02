package democompany.patterns
package worker.processStatus.v1

import democompany.patterns.domain.processStatus.v1.ProcessStatusExample.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the process status example - its topic is the process id. */
class ProcessStatusExampleWorker extends CompanyInitWorkerDsl[In, Out, NoInput, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // no variables of its own - the example is about the status the ends set
  override def customInit(in: In): NoInput = NoInput()

end ProcessStatusExampleWorker
