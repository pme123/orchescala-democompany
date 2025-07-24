package democompany.cards.domain.orderCreditcard.v1.schema


enum CardCurrency:
  case CHF, EUR, USD

object CardCurrency:
  given ApiSchema[CardCurrency]  = deriveEnumApiSchema
  given InOutCodec[CardCurrency] = deriveEnumInOutCodec
  
  object example:
    lazy val CHF = CardCurrency.CHF
    lazy val EUR = CardCurrency.EUR
    lazy val USD = CardCurrency.USD
  
enum CategoryType:
  // Main Card
  case MainCard, AdditionalCard

object CategoryType:
  given ApiSchema[CategoryType] = deriveEnumApiSchema
  given InOutCodec[CategoryType] = deriveEnumInOutCodec

  object example:
    lazy val MainCard = CategoryType.MainCard
    lazy val AdditionalCard = CategoryType.AdditionalCard

enum AddressType:
  case Billing, Shipping

object AddressType:
  given ApiSchema[AddressType] = deriveEnumApiSchema
  given InOutCodec[AddressType] = deriveEnumInOutCodec

  object example:
    lazy val Billing = AddressType.Billing
    lazy val Shipping = AddressType.Shipping

enum CardHolderType:
  case Domicile, Additional

object CardHolderType:
  given ApiSchema[CardHolderType] = deriveEnumApiSchema
  given InOutCodec[CardHolderType] = deriveEnumInOutCodec

  object example:
    lazy val Domicile = CardHolderType.Domicile
    lazy val Additional = CardHolderType.Additional
