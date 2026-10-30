package democompany.patterns.simulation

import democompany.patterns.domain.uniqueExecution.v1.*
import democompany.patterns.domain.uniqueExecution.v1.UniqueExecution.*
import orchescala.engine.domain.EngineType

/** The pattern «Einmalige Ausführung» on a real engine - the same scenarios for C7 and C8. The
  * scenarios of the pattern start with the message (name = process id); the `businessKey` is the
  * scenario name.
  */
abstract class UniqueExecutionSimulation extends CompanySimulation:

  // the engine - from CompanyC7Simulation / CompanyC8Simulation
  def engineType: EngineType

  simulate(
    // 1. started once: the user task is done, the process ends with alreadyRunning = false
    scenario(`UniqueExecution started`.startWithMsg)(
      `Edit Request UT`
    ),
    // 2. the start message comes a second time (same businessKey) while the process waits at the
    //    user task: the event sub process catches it - alreadyRunning = true, no second instance
    scenario(`UniqueExecution started twice`.startWithMsg)(
      `Start Again ME`,
      `Edit Request UT`
    ),
    // 3. invalid In: the InitWorker reports validation-failed (not handled -> incident)
    `UniqueExecution invalid In scenario`
  )

  // The start does not ask the worker app to validate the In (otherwise an invalid In is refused
  // before the process starts) - so all scenarios go through the InitWorker in the engine.
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `UniqueExecution started` =
    example

  private lazy val `UniqueExecution started twice` =
    example
      .withOut(Out.example.copy(alreadyRunning = true))

  private lazy val `UniqueExecution invalid In` =
    example
      .withIn(In.example.copy(customerName = " "))

  // Its instance stays (incident). In C7 the message of the next run (same businessKey) would land
  // in it - the pattern: 409, no process started. So C7 starts it directly. C8 has only the
  // message start (no none start event) - and its message has no correlation key, so it starts a
  // new instance anyway.
  private def `UniqueExecution invalid In scenario` =
    val incident = incidentScenario(`UniqueExecution invalid In`, "customerName must not be empty.")
    if engineType == EngineType.C8 then incident.startWithMsg else incident

  // the businessKey of the running instance is the scenario name; it waits until the InitWorker
  // set `alreadyRunning = false` - otherwise the InitWorker may overwrite the `true`
  private lazy val `Start Again ME` =
    StartAgainME.example
      .withBusinessKey("UniqueExecution started twice")
      .waitFor("alreadyRunning", false)

  private lazy val `Edit Request UT` =
    EditRequestUT.example

end UniqueExecutionSimulation
