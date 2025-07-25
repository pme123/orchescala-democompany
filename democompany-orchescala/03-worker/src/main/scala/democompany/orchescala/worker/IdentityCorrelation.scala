package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineConfig

import scala.jdk.OptionConverters.*


case class IdentityCorrelation(
    key: String,
    secret: Option[String],
    clientKeyForExternal: Option[Long] = None
):

  lazy val username: String = key match
  case ContractIdOldPattern(name) => name
  case ContractIdNewPattern(name) => name
  case ContractIdTestPattern(name) => name
  case _ => key

  lazy val asString: String =
    Seq(
      Some(key),
      secret,
      clientKeyForExternal.map(_.toString)
    )
      .filterNot:
        _.isEmpty
      .mkString:
        impersonateDiscriminator

  lazy val isTechnicalUser: Boolean = key == CompanyEngineConfig.fssoTechuserName

  lazy val identityAsString =
    s"""IdentityCorrelation:
       |- key: $key (username: $username)
       |- secret: $secret
       |- clientKeyForExternal: ${clientKeyForExternal.getOrElse("-")}
       |""".stripMargin

  private lazy val ContractIdOldPattern = s"$userPrefix(.*)".r
  private lazy val ContractIdNewPattern = s"$corrPrefix(.*);.*".r
  private lazy val ContractIdTestPattern = s"$testPrefix(.*);.*".r
end IdentityCorrelation

object IdentityCorrelation:

  def fromCorrelationString(str: String): Option[IdentityCorrelation] =
    str.split(impersonateDiscriminator).toList match
    case corrId :: corrSecret :: Nil =>
      Some(IdentityCorrelation(corrId, Some(corrSecret)))
    case userId :: Nil =>
      Some(IdentityCorrelation(userId, None))
    case corrId :: corrSecret :: clientKey :: _ =>
      Some(IdentityCorrelation(corrId, Some(corrSecret), clientKey.toLongOption))
    case Nil =>
      None

end IdentityCorrelation
