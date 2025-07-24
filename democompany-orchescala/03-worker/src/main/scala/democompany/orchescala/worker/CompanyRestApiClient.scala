package democompany.orchescala.worker

import orchescala.worker.WorkerError.ServiceAuthError
import sttp.client3.*

class CompanyRestApiClient extends RestApiClient, CompanyPasswordFlow:

  override protected def auth(
      request: Request[Either[String, String], Any]
  )(using
      context: EngineRunContext
  ): IO[ServiceAuthError, Request[Either[String, String], Any]] = ???

  end auth

end CompanyRestApiClient
