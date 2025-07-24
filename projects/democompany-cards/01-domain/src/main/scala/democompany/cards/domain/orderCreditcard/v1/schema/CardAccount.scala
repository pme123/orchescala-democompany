package democompany.cards.domain
package orderCreditcard.v1.schema

case class CardAccount(
  clientId: Long,
  accountId: Option[Long]
)
object CardAccount:
  given ApiSchema[CardAccount] = deriveApiSchema
  given InOutCodec[CardAccount] = deriveInOutCodec

  lazy val example = CardAccount(
    clientId = defaultClientId,
    accountId = Some(defaultAccountId)
  )
  
  lazy val exampleMinimal = example.copy(
    accountId = None
  )
