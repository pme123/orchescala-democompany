package democompany.orchescala.worker

import jakarta.mail.{Message, Session, Transport}
import jakarta.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import orchescala.worker.WorkerError.CustomError
import zio.*

import java.util.Properties

/** The SMTP server of the environment - locally Mailpit (`localhost:1025`), at a bank its own
  * server. Without `SMTP_USER` no login.
  */
object CompanySmtp:
  lazy val host: String             = sys.env.getOrElse("SMTP_HOST", "localhost")
  lazy val port: Int                = sys.env.getOrElse("SMTP_PORT", "1025").toInt
  lazy val from: String             = sys.env.getOrElse("SMTP_FROM", "no-reply@democompany.ch")
  lazy val startTls: Boolean        = sys.env.get("SMTP_STARTTLS").contains("true")
  lazy val user: Option[String]     = sys.env.get("SMTP_USER").filter(_.nonEmpty)
  lazy val password: Option[String] = sys.env.get("SMTP_PASSWORD")

  lazy val session: Session =
    val props = Properties()
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.port", port.toString)
    props.put("mail.smtp.starttls.enable", startTls.toString)
    props.put("mail.smtp.auth", user.isDefined.toString)
    props.put("mail.smtp.connectiontimeout", "10000")
    props.put("mail.smtp.timeout", "10000")
    Session.getInstance(props)
end CompanySmtp

/** A part of a mail besides its text - e.g. an invitation (`text/calendar; method=REQUEST`),
  * which Outlook shows as an appointment.
  */
case class MailPart(contentType: String, content: String, fileName: Option[String] = None)

/** A worker that sends mails over the SMTP server of the environment ([[CompanySmtp]]). The
  * project worker says what to send:
  * {{{
  * class SendMailWorker extends CompanyMailWorkerDsl[In, Out]:
  *   lazy val customTask = example
  *   override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
  *     sendMail(Seq(in.to), in.subject, in.body).as(Out())
  * }}}
  */
trait CompanyMailWorkerDsl[In <: Product: InOutCodec, Out <: Product: InOutCodec]
    extends CompanyCustomWorkerDsl[In, Out]:

  protected def sendMail(
      to: Seq[String],
      subject: String,
      text: String,
      parts: Seq[MailPart] = Seq.empty
  ): IO[CustomError, Unit] =
    ZIO
      .attemptBlocking:
        val message = MimeMessage(CompanySmtp.session)
        message.setFrom(InternetAddress(CompanySmtp.from))
        message.setRecipients(Message.RecipientType.TO, to.map(a => InternetAddress(a): jakarta.mail.Address).toArray)
        message.setSubject(subject, "UTF-8")
        if parts.isEmpty then message.setText(text, "UTF-8")
        else
          val body = MimeMultipart("mixed")
          val textPart = MimeBodyPart()
          textPart.setText(text, "UTF-8")
          body.addBodyPart(textPart)
          parts.foreach: p =>
            val part = MimeBodyPart()
            part.setContent(p.content, p.contentType)
            p.fileName.foreach(part.setFileName)
            body.addBodyPart(part)
          message.setContent(body)
        end if
        (CompanySmtp.user, CompanySmtp.password) match
          case (Some(u), Some(pw)) => Transport.send(message, u, pw)
          case _                   => Transport.send(message)
      .mapError: e =>
        CustomError(s"Sending the mail «$subject» to ${to.mkString(", ")} failed: ${e.getMessage}")
end CompanyMailWorkerDsl
