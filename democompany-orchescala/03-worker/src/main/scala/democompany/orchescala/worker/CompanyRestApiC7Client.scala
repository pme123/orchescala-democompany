package democompany.orchescala.worker

import orchescala.worker.WorkerError.ServiceAuthError
import orchescala.worker.oauth.TokenService
import sttp.client3.*

class CompanyRestApiC7Client extends RestApiClient, CompanyC7Client:

  override protected def auth(
                               request: Request[Either[String, String], Any]
                             )(using
                               context: EngineRunContext
                             ): ZIO[SttpClientBackend, ServiceAuthError, Request[Either[String, String], Any]] =

    given TokenService = tokenService

    ZIO
      .fromEither:
        adminToken()
      .mapError: err =>
        ServiceAuthError(
          s"Could not create Identity Correlation (fromCorrelationString) for ${context.generalVariables.impersonateUserId}.\n$err"
        )
      .map: token =>
        request.addToken(token)

  end auth

end CompanyRestApiC7Client
