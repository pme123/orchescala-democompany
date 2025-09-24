package democompany.cards
package domain.orderCreditcard.v1

object EvalResponse extends CompanyBpmnCustomTaskDsl:

  val topicName     = "democompany-cards-orderCreditcardV1-EvalResponse"
  val descr: String = "Evaluates the different responses if not approved."

  enum In:
    case None()
    case Email(emailBody: String)
    case Telephone(approvedOnTelephone: Boolean, approveOnTelephoneComment: String)

  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = CirceCodec.from(decoder, deriveInOutEncoder[In])

    // manual because of no delimiter
    lazy val decoder = new Decoder[In]:
      final def apply(c: HCursor): Decoder.Result[In] =
        (
          c.downField("emailBody").as[Option[String]],               // only if Email
          c.downField("approvedOnTelephone").as[Option[String]] // only if Telephone
        ) match
        case (Right(Some(_)), _) =>
          c.as[In.Email]
        case (_, Right(Some(_))) =>
          c.as[In.Telephone]
        case (x, y)              =>
          c.as[In.None]

    object None:
      given InOutCodec[In.None] = deriveInOutCodec
      lazy val example: In.None = In.None()
      lazy val exampleMinimal   = example // .copy(..=None)
    object Email:
      given InOutCodec[In.Email] = deriveInOutCodec
      lazy val example: In.Email = In.Email(
        emailBody = "Hello"
      )
      lazy val exampleMinimal    = example
    end Email
    object Telephone:
      given InOutCodec[In.Telephone] = deriveInOutCodec
      
      lazy val example: In.Telephone = In.Telephone(
        approvedOnTelephone = true,
        approveOnTelephoneComment = "Approved on telephone. Everything could be clarified."
      )
      lazy val exampleMinimal        = example
    end Telephone
  end In

  case class Out(
      orderCorrect: Boolean
  )
  object Out:
    given ApiSchema[Out]    = deriveApiSchema
    given InOutCodec[Out]   = deriveInOutCodec
    lazy val example        = Out(
      orderCorrect = true
    )
    lazy val exampleMinimal = example // .copy(..=None)
  end Out

  lazy val example = customTask(
    In.Email.example.asInstanceOf[In],
    Out.example
  ).withEnumInExample(In.Telephone.example)

  lazy val exampleMinimal = customTask(
    In.Email.exampleMinimal.asInstanceOf[In],
    Out.exampleMinimal
  )
end EvalResponse
