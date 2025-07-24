package democompany.orchescala.helper

import orchescala.api.*
import orchescala.helper.util.*
import democompany.orchescala.BuildInfo

object CompanyDevConfig:

  lazy val companyConfig =
    DevConfig(
      ApiProjectConfig(
        projectName = BuildInfo.name,
        projectVersion = BuildInfo.version
      )
    )

  lazy val config: DevConfig =
    config(ApiProjectConfig())

  def config(apiProjectConfig: ApiProjectConfig) = DevConfig(
    apiProjectConfig,
    // sbtConfig = companySbtConfig,
    // versionConfig = companyVersionConfig,
    // publishConfig = Some(companyPublishConfig),
    // postmanConfig = Some(companyPostmanConfig),
    // dockerConfig = companyDockerConfig,
    bpmnProcessType = BpmnProcessType.C8()
  )

  private lazy val companyVersionConfig = CompanyVersionConfig(
    scalaVersion = BuildInfo.scalaVersion,
    orchescalaVersion = BuildInfo.orchescalaV,
    companyOrchescalaVersion = BuildInfo.version,
    sbtVersion = BuildInfo.sbtVersion,
    otherVersions = Map()
  )
end CompanyDevConfig
