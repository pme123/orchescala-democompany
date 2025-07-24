package democompany.cards.domain.orderCreditcard.v1.schema


case class CardHolder(
  clientInformation: ClientInfo,
  @description("The list of cards must have at least one _Card_.")
  cards: Cards
)
object CardHolder:
  given ApiSchema[CardHolder] = deriveApiSchema
  given InOutCodec[CardHolder] = deriveInOutCodec

  lazy val example = CardHolder(
    clientInformation = ClientInfo.example,
    cards = refineUnsafe(List(ModuleCard.example))
  )
  
  lazy val exampleMinimal = CardHolder(
    clientInformation = ClientInfo.exampleMinimal,
    cards = refineUnsafe(List(ModuleCard.exampleMinimal))
  )

