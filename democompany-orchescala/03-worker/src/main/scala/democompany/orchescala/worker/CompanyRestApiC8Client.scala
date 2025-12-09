package democompany.orchescala.worker

import orchescala.engine.rest.SttpClientBackend
import orchescala.worker.WorkerError.ServiceAuthError
import sttp.client3.*

class CompanyRestApiC8Client extends RestApiClient, CompanyC8Client:

  override protected def auth(
                               request: Request[Either[String, String], Any]
                             )(using
                               context: EngineRunContext
                             ): ZIO[SttpClientBackend, ServiceAuthError, Request[Either[String, String], Any]] =

    super.auth(request) // no auth for demo

  end auth

end CompanyRestApiC8Client
