package democompany.orchescala.simulation

import democompany.orchescala.engine.{CompanyEngineC8App, CompanyEngineOpConfig}
import orchescala.engine.domain.EngineType

/** Company-specific C8 Simulation trait that works with SharedC8ClientManager - on the local c8run
  * or the Camunda 8 of the demo stack (see CompanyEngineC8Config).
  *
  * On top of the `config` of the simulation (e.g. `validateProcess(false)`): the Operate URL and,
  * with `WORKER_APP_URL`, the technical user as login for the worker apps (behind the gateway they
  * verify Bearer tokens). Without `WORKER_APP_URL` a local worker app (PresenceOnly) gets the default
  * token, as before.
  */
trait CompanyC8Simulation extends CompanySimulation, CompanyEngineC8App:
  def engineType: EngineType = EngineType.C8

  override def config: SimulationConfig =
    val withOperate = super.config.withCockpitUrl(c8OperateUrl)
    if sys.env.get("WORKER_APP_URL").exists(_.nonEmpty) then
      withOperate.withWorkerAppAuth(CompanyEngineOpConfig.adminPasswordGrant)
    else withOperate
  end config
end CompanyC8Simulation
