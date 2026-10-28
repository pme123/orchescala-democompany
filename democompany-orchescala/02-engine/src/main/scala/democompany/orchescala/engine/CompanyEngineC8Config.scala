package democompany.orchescala.engine

/** Camunda 8 - a local c8run (no authentication):
  *   - `CAMUNDA_C8_GRPC_URL` (default `http://localhost:26500`)
  *   - `CAMUNDA_C8_REST_URL` (default `http://localhost:8181`)
  *   - `CAMUNDA_C8_OPERATE_URL` (default `http://localhost:8181/operate/processes/`)
  */
trait CompanyEngineC8Config:

  lazy val c8GrpcUrl    = sys.env.getOrElse("CAMUNDA_C8_GRPC_URL", "http://localhost:26500")
  lazy val c8RestUrl    = sys.env.getOrElse("CAMUNDA_C8_REST_URL", "http://localhost:8181")
  lazy val c8OperateUrl =
    sys.env.getOrElse("CAMUNDA_C8_OPERATE_URL", "http://localhost:8181/operate/processes/")

end CompanyEngineC8Config

object CompanyEngineC8Config extends CompanyEngineC8Config
