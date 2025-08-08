package democompany.cards
package domain.orderCreditcard.v1

object CheckOrderTask extends CompanyBpmnUserTaskDsl:

  val name          = "democompany-cards-orderCreditcardV1-CheckOrderTask"
  val descr: String = ""

  case class In(
      // TODO input variables
  )
  object In:
    given ApiSchema[In]     = deriveApiSchema
    given InOutCodec[In]    = deriveInOutCodec
    lazy val example        = In()
    lazy val exampleMinimal = example // .copy(..=None)
  end In

  case class Out(
      approved: Boolean
  )
  object Out:
    given ApiSchema[Out]    = deriveApiSchema
    given InOutCodec[Out]   = deriveInOutCodec
    lazy val example        = Out(approved = true)
    lazy val exampleMinimal = example // .copy(..=None)
  end Out

  lazy val example = userTask(
    In.example,
    Out.example
  )

  lazy val exampleMinimal = userTask(
    In.exampleMinimal,
    Out.exampleMinimal
  )
end CheckOrderTask
