package democompany.services
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "democompany-services"

  lazy val projectDescr =
    "My services for the democompany"

  val version = "0.1.0-SNAPSHOT"

  document(
    clientsApi,
    mailsApi
  )


  private lazy val clientsApi =
    import domain.clients.v1.*
    group(ClientsV1.serviceLabel)(
      GetClientsClientId.example
    )
  private lazy val mailsApi =
    import domain.mails.v1.*
    group(MailsV1.serviceLabel)(
      SendEmail.example
    )

end ApiProjectCreator
