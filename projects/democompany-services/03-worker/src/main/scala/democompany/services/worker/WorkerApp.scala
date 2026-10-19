package democompany.services.worker

// sbt worker/run
object WorkerApp extends CompanyWorkerApp:
  workers(
    clientsV1Workers,
    mailsV1Workers,
    smtpV1Workers
  )
  dependencies(
  )

  private lazy val clientsV1Workers = Seq(
    clients.v1.GetClientsClientIdWorker()
  )
  private lazy val mailsV1Workers = Seq(
    mails.v1.SendEmailWorker()
  )
  // mails over the SMTP of the environment (locally Mailpit)
  private lazy val smtpV1Workers = Seq(
    smtp.v1.SendMailWorker()
  )
end WorkerApp
