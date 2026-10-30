package democompany.patterns
package worker.identity.v1

import democompany.patterns.domain.identity.v1.CheckIdentity.*

/** Adds the username of the identity the process has at this point to `checkedUsernames` - the
  * BPMN maps it in as `identityUsername`.
  */
class CheckIdentityWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    Right(Out(checkedUsernames = in.checkedUsernames.getOrElse(Seq.empty) :+ in.identityUsername))

end CheckIdentityWorker
