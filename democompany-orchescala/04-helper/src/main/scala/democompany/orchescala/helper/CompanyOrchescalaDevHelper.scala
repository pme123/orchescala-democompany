package democompany.orchescala.helper

import orchescala.api.ApiConfig
import orchescala.helper.dev.DevCompanyOrchescalaHelper
import orchescala.helper.util.DevConfig
import democompany.orchescala.BuildInfo
import democompany.orchescala.api.CompanyApiCreator

object CompanyOrchescalaDevHelper
    extends DevCompanyOrchescalaHelper:

  lazy val apiConfig: ApiConfig = CompanyApiCreator.apiConfig
    .copy(
      basePath = os.pwd / "00-docs",
      tempGitDir = os.pwd / os.up /  os.up / "git-temp"
    )

  lazy val devConfig: DevConfig = CompanyDevConfig.companyConfig

end CompanyOrchescalaDevHelper
