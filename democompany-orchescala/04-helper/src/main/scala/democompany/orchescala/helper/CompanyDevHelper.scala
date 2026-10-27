package democompany.orchescala.helper

import orchescala.api.ApiConfig
import orchescala.helper.dev.DevHelper
import orchescala.helper.util.DevConfig
import democompany.orchescala.api.CompanyApiCreator

case object CompanyDevHelper
    extends DevHelper:

  lazy val apiConfig: ApiConfig = CompanyApiCreator.apiConfig
  lazy val devConfig: DevConfig = CompanyDevConfig.config

  // deploy through the gateway of the company (see CompanyDeployHelper) - not with Postman
  override def run(command: String, arguments: String*): Unit =
    (command, arguments.toList) match
      case ("deploy", simulation :: engine) if engine.size <= 1 =>
        CompanyDeployHelper(devConfig)
          .deploy(Some(simulation), CompanyDeployHelper.engineType(simulation, engine.headOption))
      case _                                                    =>
        super.run(command, arguments*)

end CompanyDevHelper
