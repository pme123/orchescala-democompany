package democompany.patterns
package worker.initProcess.v1

import democompany.patterns.domain.initProcess.v1.InitProcess.*
import orchescala.worker.WorkerError.ValidatorError

/** The InitWorker of the pattern «Init Process» - its topic is the process id. */
class InitProcessWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  given InOutCodec[InConfig] = InConfig.given_InOutCodec_InConfig

  lazy val inOutExample = example

  // an empty name is not valid - the worker reports `validation-failed`
  override def validate(in: In): Either[ValidatorError, In] =
    if in.customerName.trim.isEmpty then
      Left(ValidatorError("customerName must not be empty."))
    else Right(in)

  // inConfig is set here - the worker merged the defaults of the InConfig into it
  override def customInit(in: In): InitIn =
    val config = in.inConfig.getOrElse(in.defaultConfig)
    InitIn(greetingMessage = s"${config.greeting} ${in.customerName}")

end InitProcessWorker
