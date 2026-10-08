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
  // Kundentermine - on Operaton, i.e. the BPMN of Camunda 7 (src/main/resources/camunda)
  lazy val `democompany-customer` =
    ProjectConfig("democompany-customer", group = customer, color = "#d9d2f5", bpmnProcessType = BpmnProcessType.C7())

  lazy val projects: Seq[ProjectConfig] = Seq(
    `democompany-cards`,
    `democompany-customer`,
    `democompany-services`
  )

  private lazy val general = ProjectGroup("general", color = "green")
  private lazy val customer = ProjectGroup("customer", color = "#7252ac", fill = "#efeafb")

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