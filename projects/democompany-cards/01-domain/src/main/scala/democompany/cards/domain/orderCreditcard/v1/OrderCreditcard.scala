package democompany.cards
package domain.orderCreditcard.v1

import democompany.services.domain.clients.v1.GetClientsClientId
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
    lazy val example = In(
      clientId = 1000,
      creditCardAccount = CardAccount.example,
      mainCardHolder = Some(CardHolder.example),
      inConfig = None
    )
    lazy val exampleMinimal = example.copy(
      creditCardAccount = CardAccount.exampleMinimal,
      mainCardHolder = None
    )
    
  case class InConfig(
      // Process Configuration
      @description("To test cancel from other processes you need to set this flag.")
      timerNotReceivedEmail: String = "P3D",
      // Mocks
      @description(serviceOrProcessMockDescr(GetClientsClientId.Out.example))
      getClientMock: Option[GetClientsClientId.Out] = None
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
      simpleValue = Some("PETER PAN")
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
    lazy val example = Out(
      processStatus = ProcessStatus.succeeded,
      creditCardAccount = CardAccount.example,
      initCreditCardAccount = CardAccount.example.copy(accountId = None),
      simpleValue = Some("PETER PAN")
    )
    lazy val exampleMinimal = example.copy(
      simpleValue = None,
      creditCardAccount = CardAccount.exampleMinimal,
    )

  lazy val example = process(
    In.example,
    Out.example,
    InitIn.example
  )

  lazy val exampleMinimal = process(
    In.exampleMinimal,
    Out.exampleMinimal,
    InitIn.example
  )
end OrderCreditcard
