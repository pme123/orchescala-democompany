package democompany.orchescala.helper

import orchescala.engine.domain.EngineType
import orchescala.helper.dev.deploy.DeployHelper
import orchescala.helper.util.DevConfig
import sttp.client3.*

import scala.concurrent.duration.*

/** `./helper.scala deploy <simulation> [engine]` - like the Postman deploy of Orchescala (BPF's
  * `apply-manifest`), but through the gateway of the company: it reads the BPMN/DMN from the
  * project's jar in `~/.ivy2/local` (`camunda` for C7/Op, `camunda8` for C8) and deploys them to the
  * engine it knows.
  *
  *   1. `sbt publishLocal`
  *   1. `POST <GATEWAY_URL>/deployment?targetEngine=<engine>` with the manifest of the project
  *   1. `sbt simulation/testOnly *<simulation>` (`all` for all simulations)
  *
  * `GATEWAY_URL` (default `http://localhost:8888`), `GATEWAY_TOKEN` (default `local-dev` - enough for
  * a gateway without `TOKEN_ISSUER`, it only checks that there is a token).
  */
case class CompanyDeployHelper(devConfig: DevConfig):

  private lazy val gatewayUrl   =
    sys.env.get("GATEWAY_URL").filter(_.nonEmpty).getOrElse("http://localhost:8888").stripSuffix("/")
  private lazy val gatewayToken = sys.env.get("GATEWAY_TOKEN").filter(_.nonEmpty).getOrElse("local-dev")

  def deploy(simulation: Option[String], engineType: EngineType): Unit =
    val start = System.currentTimeMillis()
    run("sbt", "publishLocal")
    deployManifest(engineType)
    simulation.foreach: sim =>
      val testName = if sim == "all" then "" else sim
      run("sbt", "-J-Xmx3G", s"simulation/testOnly *$testName")
    println(s"Deploy and test finished in ${(System.currentTimeMillis() - start) / 1000} s")
  end deploy

  private def deployManifest(engineType: EngineType): Unit =
    val project  = devConfig.projectName
    val version  = devConfig.apiProjectConfig.projectVersion.toString
    val company  = devConfig.companyName
    val manifest =
      s"""{"deployments":[{"company":"$company","project":"$project","version":"$version"}]}"""
    println(s"Deploying $company:$project:$version to $engineType via $gatewayUrl")
    val response = basicRequest
      .post(uri"$gatewayUrl/deployment?targetEngine=$engineType")
      .header("Authorization", s"Bearer $gatewayToken")
      .contentType("application/json")
      .body(manifest)
      .readTimeout(2.minutes)
      .response(asStringAlways)
      .send(HttpClientSyncBackend())
    if !response.code.isSuccess then
      throw IllegalStateException(
        s"Deploy through the gateway failed: ${response.code.code} ${response.body.take(500)}"
      )
    println(s"Deployed: ${response.body}")
  end deployManifest

  private def run(command: String*): Unit =
    println(command.mkString(" "))
    os.proc(command).call(cwd = os.pwd, stdout = os.Inherit, stderr = os.Inherit)

end CompanyDeployHelper

object CompanyDeployHelper:

  /** The arguments of `deploy` as Orchescala reads them: `<simulation> [engine]` - without engine
    * from the name of the simulation (`..C8Simulation` -> C8, else C7).
    */
  def engineType(simulation: String, engine: Option[String]): EngineType =
    engine
      .map(e => EngineType.values.find(_.toString.equalsIgnoreCase(e)).getOrElse(
        throw IllegalArgumentException(s"Unknown engine '$e' - use ${EngineType.values.mkString("|")}")
      ))
      .getOrElse(DeployHelper.engineTypeFromSimulation(simulation))
end CompanyDeployHelper
