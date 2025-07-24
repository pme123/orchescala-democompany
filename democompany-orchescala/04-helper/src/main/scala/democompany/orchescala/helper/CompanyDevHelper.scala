package democompany.orchescala.helper

import orchescala.api.ApiConfig
import orchescala.helper.dev.DevHelper
import orchescala.helper.util.DevConfig
import democompany.orchescala.api.CompanyApiCreator

case object CompanyDevHelper
    extends DevHelper:

  lazy val apiConfig: ApiConfig = CompanyApiCreator.apiConfig
  lazy val devConfig: DevConfig = CompanyDevConfig.config

end CompanyDevHelper
