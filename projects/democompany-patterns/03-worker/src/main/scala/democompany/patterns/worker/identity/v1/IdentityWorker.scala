package democompany.patterns
package worker.identity.v1

import democompany.patterns.domain.identity.v1.Identity.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the identity example - its topic is the process id. */
class IdentityWorker extends CompanyInitWorkerDsl[In, Out, NoInput, NoInput]:

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // no variables of its own - the example is about the identity after the user task
  override def customInit(in: In): NoInput = NoInput()

end IdentityWorker
