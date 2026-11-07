package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineOpConfig
import orchescala.engine.rest.{PasswordGrantFlow, SttpClientBackend}
import zio.ZIO

/** The worker app deploys the BPMN/DMN of its project when it starts (Orchescala's
  * `DeployOnStart`) - with `DEPLOY_ON_START_GATEWAY_URL`, through that gateway, to the engines of
  * `COMPANY_ENGINES`. Without it nothing is deployed.
  *
  * The token for the gateway: `GATEWAY_TOKEN` (e.g. `local-dev` for a gateway without
  * `TOKEN_ISSUER`), else one of the technical user (`SSO_TECHUSER_NAME` / `SSO_TECHUSER_PASSWORD`).
  */
object CompanyDeployOnStart:

  lazy val fromEnv: Option[DeployOnStart] =
    sys.env.get("DEPLOY_ON_START_GATEWAY_URL").filter(_.nonEmpty).map(DeployOnStart(_, token))

  private lazy val token: ZIO[SttpClientBackend, Any, String] =
    sys.env.get("GATEWAY_TOKEN").filter(_.nonEmpty) match
      case Some(token) => ZIO.succeed(token)
      case None        => techUserFlow.retrieveToken()

  private lazy val techUserFlow = PasswordGrantFlow(CompanyEngineOpConfig.adminPasswordGrant)

end CompanyDeployOnStart
