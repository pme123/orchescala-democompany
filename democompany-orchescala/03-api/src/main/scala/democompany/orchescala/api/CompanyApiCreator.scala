package democompany.orchescala
package api

/**
 * Add here company specific stuff, to create the Api documentation and the Postman collection.
 */
trait CompanyApiCreator extends ApiCreator, ApiDsl, CamundaPostmanApiCreator:

  // override the config if needed
  protected def apiConfig: ApiConfig = CompanyApiCreator.apiConfig

  lazy val companyProjectVersion = BuildInfo.version

object CompanyApiCreator:
   lazy val apiConfig = ApiConfig(companyName = "democompany")
