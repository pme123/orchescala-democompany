package democompany.orchescala.engine

import orchescala.engine.auth.TokenValidation

/** How Bearer tokens are checked - by the gateway and by the worker apps (workers with roles need
  * verified tokens), per environment from the env:
  *
  * `TOKEN_ISSUER` (as in the tokens, e.g. `http://localhost:8182/auth/realms/democompany`): the
  * tokens are verified (signature, issuer, expiry); the keys from `TOKEN_JWKS_URL` (e.g. the IdP
  * inside the cluster), else from the issuer. `TOKEN_AUDIENCE`: the token must name one of them
  * (comma separated). Without `TOKEN_ISSUER` only their presence is checked. The gateway also reads
  * the older names `GATEWAY_TOKEN_*`.
  */
object CompanyTokenValidation:

  lazy val fromEnv: TokenValidation =
    env("ISSUER") match
      case None         => TokenValidation.PresenceOnly
      case Some(issuer) =>
        TokenValidation.Jwt(
          issuer = issuer,
          jwksUrl = env("JWKS_URL"),
          audience = env("AUDIENCE").toSeq.flatMap(_.split(",")).map(_.trim).filter(_.nonEmpty)
        )

  private def env(name: String): Option[String] =
    sys.env.get(s"TOKEN_$name").orElse(sys.env.get(s"GATEWAY_TOKEN_$name")).filter(_.nonEmpty)
end CompanyTokenValidation
