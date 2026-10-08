package democompany.orchescala.gateway

import democompany.orchescala.engine.{CompanyEngineOpConfig, CompanyTokenValidation}
import orchescala.engine.auth.TokenValidation
import orchescala.gateway.PublicAccess

/** The settings of the gateway per environment - from the env. */
object CompanyGatewayConfig:

  /** See [[CompanyTokenValidation]] - the same for the worker apps. */
  lazy val tokenValidation: TokenValidation = CompanyTokenValidation.fromEnv

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
