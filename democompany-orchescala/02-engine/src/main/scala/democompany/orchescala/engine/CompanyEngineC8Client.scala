package democompany.orchescala.engine

import democompany.orchescala.engine.CompanyEngineC7Config.*
import io.circe.parser.*
import orchescala.engine.c7.*
import orchescala.engine.c8.C8SaasClient
import orchescala.engine.domain.EngineError
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.camunda.community.rest.client.invoker.ApiClient
import zio.ZIO

import scala.io.Source
import scala.jdk.CollectionConverters.*

trait CompanyEngineC8Client extends C8SaasClient, CompanyEngineC8Config:
  
end CompanyEngineC8Client
