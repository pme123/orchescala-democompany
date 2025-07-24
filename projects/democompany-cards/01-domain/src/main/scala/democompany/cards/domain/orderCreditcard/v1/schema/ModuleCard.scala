package democompany.cards.domain
package orderCreditcard.v1.schema



type CardsV = collection.MinLength[1]
type Cards = List[ModuleCard] :| CardsV

case class ModuleCard(
    categoryType: CategoryType,
    cardLimit: Option[Double],
    cardCurrency: Option[CardCurrency],
    embossedLineOne: String
)
object ModuleCard:
  given ApiSchema[ModuleCard]  = deriveApiSchema
  given InOutCodec[ModuleCard] = deriveInOutCodec

  lazy val example = ModuleCard(
    categoryType = CategoryType.MainCard,
    cardLimit = Some(10 * 1000),
    cardCurrency = Some(CardCurrency.CHF),
    embossedLineOne = "PETER PAN"
  )
  
  lazy val exampleMinimal = example.copy(
    cardLimit = None,
    cardCurrency = None
  )
