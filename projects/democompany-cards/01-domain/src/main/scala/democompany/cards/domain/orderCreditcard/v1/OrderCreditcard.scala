package democompany.cards
package domain.orderCreditcard.v1

import schema.*

object OrderCreditcard extends CompanyBpmnProcessDsl:

  val processName   = "democompany-cards-orderCreditcardV1"
  val descr: String = "Demo Process to order a credit card."

  case class In(
      clientId: Long,
      creditCardAccount: CardAccount,
      mainCardHolder: Option[CardHolder],
      @description(
        "A way to override process configuration.\n\n**SHOULD NOT BE USED on Production!**"
      )
      inConfig: Option[InConfig]
  ) extends WithConfig[InConfig]:
    lazy val defaultConfig = InConfig()
  end In
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec

  case class InConfig(
      // Process Configuration
      // @description("To test cancel from other processes you need to set this flag.")
      //  waitForCancel: Boolean = false,
      // Mocks
      // outputServiceMock
      // @description(serviceOrProcessMockDescr(GetRelationship.serviceMock))
      // getRelationshipMock: Option[MockedServiceResponse[GetRelationship.ServiceOut]] = None,
      // outputMock
      // @description(serviceOrProcessMockDescr(GetContractContractKey.Out()))
      // getContractMock: Option[GetContractContractKey.Out] = None
  )
  object InConfig:
    given ApiSchema[InConfig]  = deriveApiSchema
    given InOutCodec[InConfig] = deriveInOutCodec

  // type InitIn = NoInput // if no initialisation is needed
  case class InitIn(
      initCreditCardAccount: CardAccount,
      simpleValue: Option[String]
  )

  object InitIn:
    given ApiSchema[InitIn]  = deriveApiSchema
    given InOutCodec[InitIn] = deriveInOutCodec

    lazy val example = InitIn(
      initCreditCardAccount = CardAccount.example.copy(accountId = None),
      simpleValue = Some("Hello World")
    )
    lazy val exampleMinimal = example.copy(
      simpleValue = None
    )
  end InitIn

  case class Out(
      processStatus: ProcessStatus,
      creditCardAccount: CardAccount,
      initCreditCardAccount: CardAccount,
      simpleValue: Option[String]
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec

  lazy val inExample = In(
    clientId = 1000,
    creditCardAccount = CardAccount.example,
    mainCardHolder = Some(CardHolder.example),
    inConfig = None
  )

  lazy val inExampleMinimal = inExample.copy(
    clientId = 1,
    creditCardAccount = CardAccount.exampleMinimal,
    mainCardHolder = None
  )

  lazy val outExample = Out(
    processStatus = ProcessStatus.succeeded,
    creditCardAccount = CardAccount.example,
    initCreditCardAccount = CardAccount.example.copy(accountId = None),
    simpleValue = Some("Hello World")
  )

  lazy val outExampleMinimal = outExample.copy(
    simpleValue = None,
    creditCardAccount = CardAccount.exampleMinimal,
  )

  lazy val example = process(
    inExample,
    outExample,
    InitIn.example
  )

  lazy val exampleMinimal = process(
    inExampleMinimal,
    outExampleMinimal,
    InitIn.example
  )
end OrderCreditcard
