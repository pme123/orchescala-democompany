package democompany.orchescala
package api

import democompany.orchescala.api.CompanyApiCreator.projectsConfig
import democompany.orchescala.engine.companyEngineConfig

/**
 * Add here company specific stuff, to create the Api documentation and the Postman collection.
 */
trait CompanyApiCreator extends ApiCreator, ApiDsl, CamundaPostmanApiCreator:

  // override the config if needed
  protected def apiConfig: ApiConfig =
    CompanyApiCreator
      .apiConfig
      .withProjectsConfig(projectsConfig)

  lazy val companyProjectVersion = BuildInfo.version

object CompanyApiCreator:
  lazy val apiConfig = ApiConfig(engineConfig = companyEngineConfig, companyName = "democompany")

  lazy val projectsConfig = ProjectsConfig(
    perGitRepoConfigs = Seq(
      groupedProjectConfig
    )
  )

  private lazy val groupedProjectConfig = ProjectsPerGitRepoConfig(
    "ssh://git@github.com/pme123",
    projects,
    singleRepo = true
  )

  lazy val `democompany-services` = generalProjectConfig("democompany-services", "#ffffcc")
  lazy val `democompany-cards` = generalProjectConfig("democompany-cards", "#c8feda")

  lazy val projects: Seq[ProjectConfig] = Seq(
    `democompany-cards`,
    `democompany-services`
  )

  private lazy val general = ProjectGroup("general", color = "green")

  def generalProjectConfig(projectName: String, color: String) =
    projectConfig(projectName, color, general)

  def projectConfig(
                     projectName: String,
                     color: String,
                     projectGroup: ProjectGroup
                   ) =
    ProjectConfig(
      projectName,
      group = projectGroup,
      color = color,
      bpmnProcessType = BpmnProcessType.C8()
    )