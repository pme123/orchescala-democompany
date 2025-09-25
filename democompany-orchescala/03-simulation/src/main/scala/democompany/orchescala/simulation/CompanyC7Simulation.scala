package democompany.orchescala.simulation

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
trait CompanyC7Simulation extends CompanySimulation, C7Client:
  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] = Seq(
    SharedC7ClientManager.layer
  )

  lazy val client: ZIO[SharedC7ClientManager, EngineError, ApiClient] =
    SharedC7ClientManager.getOrCreateClient:
      (for
        _      <- ZIO.logDebug("Creating API Client")
        client <- ZIO.attempt(ApiClient())
        _      <- ZIO.attempt:
                    client.setBasePath(camundaRestUrl)
        token  <- getOAuthTokenZIO()
        _      <- ZIO.attempt:
                    client.addDefaultHeader("Authorization", s"Bearer $token")
      yield client)
        .mapError: ex =>
          EngineError.UnexpectedError(s"Problem creating API Client: $ex")
    // OAuth configuration

  private def getOAuthTokenZIO() =
    ZIO.attempt:
      // Create OAuth client
      val oauthClient = HttpClients.custom().build()

      // Request parameters for OAuth token
      val params   = Map(
        "grant_type"    -> "client_credentials",
        "client_id"     -> fssoClientName,
        "client_secret" -> fssoClientSecret,
        "scope"         -> fssoScope
      )
      // Set OAuth2 access token for authorization
      val tokenUrl =
        s"$fssoBaseUrl/auth/realms/$fssoRealm/protocol/openid-connect/token"

      // Get token and set it in the ApiClient
      getOAuthToken(oauthClient, tokenUrl, params)

  // Helper method to get OAuth token
  private def getOAuthToken(
      httpClient: org.apache.hc.client5.http.impl.classic.CloseableHttpClient,
      tokenUrl: String,
      params: Map[String, String]
  ): String =
    // Create form parameters
    val formParams = params.map { case (key, value) =>
      new BasicNameValuePair(key, value)
    }.toList.asJava

    // Build request
    val request = ClassicRequestBuilder.post(tokenUrl)
      .setEntity(new UrlEncodedFormEntity(formParams))
      .build()

    // Execute request and parse response
    val response = httpClient.execute(request)
    val entity   = response.getEntity
    val content  = Source.fromInputStream(entity.getContent).mkString

    // Parse JSON response to get access token
    val json        = parse(content).getOrElse(throw new Exception("Failed to parse OAuth response"))
    val accessToken = json.hcursor.get[String]("access_token").getOrElse(
      throw new Exception("Failed to extract access token from OAuth response")
    )

    accessToken
  end getOAuthToken

  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    C7ProcessEngine.withClient(this)
      .provideLayer(SharedC7ClientManager.layer)

end CompanyC7Simulation

object CompanyC7Simulation extends CompanyC7Simulation
