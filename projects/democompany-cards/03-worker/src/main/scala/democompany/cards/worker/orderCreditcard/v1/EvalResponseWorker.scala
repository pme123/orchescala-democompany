package democompany.cards
package worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.EvalResponse.*

class EvalResponseWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    in match
      case In.None() => Right(Out(orderCorrect = false))
      case In.Email(_) => Right(Out(orderCorrect = true))
      case In.Telephone(approvedOnTelephone, _) => Right(Out(
        orderCorrect = approvedOnTelephone
      ))
  end runWork

end EvalResponseWorker