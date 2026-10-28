package democompany.orchescala.engine

import orchescala.engine.c8.C8BearerTokenClient

/** The caller's token is passed through - a local c8run doesn't need one. */
trait CompanyEngineC8Client extends C8BearerTokenClient, CompanyEngineC8Config:

  protected def zeebeGrpc: String = c8GrpcUrl
  protected def zeebeRest: String = c8RestUrl

end CompanyEngineC8Client
