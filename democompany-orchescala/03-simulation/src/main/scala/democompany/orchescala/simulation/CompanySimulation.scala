package democompany.orchescala.simulation

import democompany.orchescala.engine.CompanyEngineConfig.*
import orchescala.engine.c7.C7ProcessEngine
import orchescala.engine.{EngineConfig, EngineError, ProcessEngine}
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.camunda.community.rest.client.invoker.ApiClient
import zio.{IO, ZIO}
import io.circe.parser.*
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.apache.hc.core5.http.message.BasicNameValuePair

import scala.io.Source
import scala.jdk.CollectionConverters.*


/** Add here company specific stuff, to run the Simulations.
  */
trait CompanySimulation extends SimulationRunner:

  given IO[EngineError, ApiClient] =
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
        s"${fssoBaseUrl}/auth/realms/${config.tenantId.get}/protocol/openid-connect/token"

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

  lazy val engine: ProcessEngine = C7ProcessEngine()

  override def config =
    super.config

  given EngineConfig = EngineConfig(tenantId = config.tenantId)

end CompanySimulation
