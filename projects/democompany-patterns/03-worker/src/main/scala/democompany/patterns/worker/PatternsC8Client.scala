package democompany.patterns.worker

import orchescala.engine.c8.C8NoAuthClient

/** C8 without authentication - a local c8run (the company default is the SaaS cluster).
  *   - `C8_LOCAL_GRPC_URL` (default `http://localhost:26500`)
  *   - `C8_LOCAL_REST_URL` (default `http://localhost:8181`)
  */
object PatternsC8Client extends C8NoAuthClient:
  lazy val zeebeGrpc: String = sys.env.getOrElse("C8_LOCAL_GRPC_URL", "http://localhost:26500")
  lazy val zeebeRest: String = sys.env.getOrElse("C8_LOCAL_REST_URL", "http://localhost:8181")
