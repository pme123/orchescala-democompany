package democompany.orchescala.gateway

import democompany.orchescala.engine.CompanyEngineOpConfig
import orchescala.engine.auth.TokenValidation
import orchescala.gateway.PublicAccess

/** The settings of the gateway per environment - from the env. */
object CompanyGatewayConfig:

  /** `GATEWAY_TOKEN_ISSUER` (as in the tokens, e.g. `http://localhost:8182/auth/realms/democompany`):
    * the Bearer tokens are verified (signature, issuer, expiry); the keys from `GATEWAY_TOKEN_JWKS_URL`
    * (e.g. the IdP inside the cluster), else from the issuer. `GATEWAY_TOKEN_AUDIENCE`: the token must
    * name one of them (comma separated). Without `GATEWAY_TOKEN_ISSUER` only their presence is checked.
    */
  lazy val tokenValidation: TokenValidation =
    sys.env.get("GATEWAY_TOKEN_ISSUER").filter(_.nonEmpty) match
      case None         => TokenValidation.PresenceOnly
      case Some(issuer) =>
        TokenValidation.Jwt(
          issuer = issuer,
          jwksUrl = sys.env.get("GATEWAY_TOKEN_JWKS_URL").filter(_.nonEmpty),
          audience = list("GATEWAY_TOKEN_AUDIENCE").toSeq
        )

  /** What is reachable without a token (`/public/...`): `PUBLIC_WORKERS`, `PUBLIC_PROCESSES`,
    * `PUBLIC_MESSAGES` (comma separated). Inside, the gateway logs in as the technical user of the
    * company. `PUBLIC_REQUESTS_PER_MINUTE` per client (default 30), `PUBLIC_CLIENT_IP_HEADER` behind
    * a proxy.
    */
  lazy val publicAccess: PublicAccess =
    PublicAccess(
      workers = list("PUBLIC_WORKERS"),
      processStarts = list("PUBLIC_PROCESSES"),
      messages = list("PUBLIC_MESSAGES"),
      login = Some(CompanyEngineOpConfig.adminPasswordGrant),
      requestsPerMinute = sys.env.get("PUBLIC_REQUESTS_PER_MINUTE").map(_.toInt).getOrElse(30),
      clientIpHeader = sys.env.get("PUBLIC_CLIENT_IP_HEADER").filter(_.nonEmpty)
    )

  private def list(name: String): Set[String] =
    sys.env.get(name).toSeq.flatMap(_.split(",")).map(_.trim).filter(_.nonEmpty).toSet
end CompanyGatewayConfig
