package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.SendMail.*

/** A mail over the SMTP of the environment - the text comes from the BPMN, `\n` is a line break. */
class SendMailWorker extends CompanyMailWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    sendMail(Seq(in.to), in.subject, SendMailWorker.text(in.body)).as(Out(LocalDateTime.now()))

end SendMailWorker

object SendMailWorker:
  /** In a BPMN text a line break is `\n` (two characters). */
  def text(body: String): String = body.replace("\\n", "\n")
