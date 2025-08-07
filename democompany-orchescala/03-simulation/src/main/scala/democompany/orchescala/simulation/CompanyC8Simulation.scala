package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC8Config
import democompany.orchescala.engine.CompanyEngineC8Config.*
import io.camunda.client.CamundaClient
import orchescala.engine.c8.*
import orchescala.engine.{EngineError, ProcessEngine}
import zio.{IO, ZIO}

import java.net.URI

/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8Config, C8SaasClient:
  given IO[EngineError, CamundaClient] = client

  lazy val engine: ProcessEngine = C8ProcessEngine()

  override lazy val config: SimulationConfig =
    SimulationConfig(
      endpoint = zeebeRest
    )
    
end CompanyC8Simulation
