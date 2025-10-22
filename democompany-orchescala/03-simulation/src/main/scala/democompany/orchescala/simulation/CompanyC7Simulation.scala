package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineC7Client
import democompany.orchescala.engine.CompanyEngineC7Config.*
import io.circe.parser.*
import orchescala.engine.ProcessEngine
import orchescala.engine.c7.*
import orchescala.engine.domain.EngineError
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.camunda.community.rest.client.invoker.ApiClient
import zio.{ZIO, ZLayer}

import scala.io.Source
import scala.jdk.CollectionConverters.*


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanyC7Simulation extends CompanySimulation, CompanyEngineC7Client:
  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC7ClientManager.layer
  )

  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(this)
      .provideLayer(SharedC7ClientManager.layer)

end CompanyC7Simulation

object CompanyC7Simulation extends CompanyC7Simulation
