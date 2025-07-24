package democompany.cards.domain
package orderCreditcard.v1.schema


type ResidencePermitCode = Int :| any.In[(1, 2, 3, 4, 5, 6, 7, 8)]
case class ClientInfo(
  clientId: Long,
  addressType: CardHolderType,
  residencePermitCode: Option[ResidencePermitCode]
)
object ClientInfo:
  given ApiSchema[ClientInfo] = deriveApiSchema
  given InOutCodec[ClientInfo] = deriveInOutCodec

  lazy val example = ClientInfo(
    clientId = defaultClientId,
    addressType = CardHolderType.Domicile,
    residencePermitCode = Some(2)
  )
  
  lazy val exampleMinimal = example.copy(
    residencePermitCode = None
  )
