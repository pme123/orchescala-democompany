package democompany.orchescala.helper

import orchescala.api.ApiConfig
import orchescala.engine.EngineConfig
import orchescala.helper.dev.DevCompanyOrchescalaHelper
import orchescala.helper.util.DevConfig
import democompany.orchescala.BuildInfo
import democompany.orchescala.api.CompanyApiCreator
import democompany.orchescala.engine.companyEngineConfig

object CompanyOrchescalaDevHelper
    extends DevCompanyOrchescalaHelper:

  // with the projects of the company - for the docs (catalog, release, dependencies, site)
  lazy val apiConfig: ApiConfig = CompanyApiCreator.apiConfig
    .withProjectsConfig(CompanyApiCreator.projectsConfig)
    .copy(
      basePath = os.pwd / "00-docs",
      tempGitDir = os.pwd / os.up /  os.up / "git-temp"
    )

  lazy val engineConfig: EngineConfig = companyEngineConfig
  lazy val devConfig: DevConfig       = CompanyDevConfig.companyConfig

end CompanyOrchescalaDevHelper
