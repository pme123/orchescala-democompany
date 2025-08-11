package democompany.services
package domain.mails.v1

object SendEmail extends MailsV1:

  val topicName     = "democompany-services-mailsV1-SendEmail"
  val descr: String = "Send an Email"

  val path = "PUT: mail"

  case class In(
      subject: String,
      body: String,
      to: String,
      cc: Option[String],
      bcc: Option[String]
  )
  object In:
    given ApiSchema[In]     = deriveApiSchema
    given InOutCodec[In]    = deriveInOutCodec
    lazy val example        = In(
      subject = "Request for Information",
      body =
        """Dear John, how are you?
          |We need more information.
          | - Passport or Drivers License
          | - Proof of Address
          |Kind regards, Jane""".stripMargin,
      to = "john.doe@example.com",
      cc = Some("jane.doe@example.com"),
      bcc = None
    )
    lazy val exampleMinimal = example.copy(cc = None)
  end In

  case class Out(
  )
  object Out:
    given ApiSchema[Out]    = deriveApiSchema
    given InOutCodec[Out]   = deriveInOutCodec
    lazy val example        = Out()
    lazy val exampleMinimal = example // .copy(..=None)
  end Out

  type ServiceIn  = In       // just need to format the body to html
  type ServiceOut = NoOutput // if no output is needed

  object ServiceIn:
    lazy val example        = In.example.copy(body = In.example.body.replaceAll("\n", "</br>"))
    lazy val exampleMinimal = In.exampleMinimal.copy(body = In.exampleMinimal.body.replaceAll("\n", "</br>"))

  object ServiceOut:
    lazy val example        = NoOutput()                       // or ...example
    lazy val exampleMinimal = example                          // .copy(..=None)
    lazy val mock           = MockedServiceResponse.success204 // or MockedServiceResponse.success200(example)
    lazy val mockMinimal    =
      MockedServiceResponse.success204 // or MockedServiceResponse.success200(exampleMinimal)
  end ServiceOut

  lazy val example = serviceTask(
    In.example,
    Out.example,
    ServiceOut.mock,
    ServiceIn.example
  )

  lazy val exampleMinimal = serviceTask(
    In.exampleMinimal,
    Out.exampleMinimal,
    ServiceOut.mockMinimal,
    ServiceIn.exampleMinimal
  )
end SendEmail
