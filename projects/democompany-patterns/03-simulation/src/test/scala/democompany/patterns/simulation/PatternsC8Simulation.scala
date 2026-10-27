package democompany.patterns.simulation

import democompany.orchescala.engine.companyEngineConfig
import orchescala.engine.{EngineConfig, ProcessEngine}
import orchescala.engine.c8.{C8NoAuthClient, C8ProcessEngine, SharedC8ClientManager}
import orchescala.engine.domain.EngineType
import zio.{ZIO, ZLayer}

/** Like CompanyC8Simulation - but on a local c8run without authentication (the company default is
  * the SaaS cluster).
  *   - `C8_LOCAL_GRPC_URL` (default `http://localhost:26500`)
  *   - `C8_LOCAL_REST_URL` (default `http://localhost:8181`)
  */
trait PatternsC8Simulation extends CompanySimulation, C8NoAuthClient:
  def engineType: EngineType = EngineType.C8

  given EngineConfig = companyEngineConfig

  lazy val zeebeGrpc: String = sys.env.getOrElse("C8_LOCAL_GRPC_URL", "http://localhost:26500")
  lazy val zeebeRest: String = sys.env.getOrElse("C8_LOCAL_REST_URL", "http://localhost:8181")

  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C8ProcessEngine.withClient(this)
      .provideLayer(SharedC8ClientManager.layer)

  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC8ClientManager.layer
  )

  override def config =
    super.config
      .withCockpitUrl(s"$zeebeRest/operate/processes/")

end PatternsC8Simulation
