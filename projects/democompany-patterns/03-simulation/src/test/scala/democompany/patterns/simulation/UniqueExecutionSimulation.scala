package democompany.patterns.simulation

import democompany.patterns.domain.uniqueExecution.v1.*
import democompany.patterns.domain.uniqueExecution.v1.UniqueExecution.*

/** The pattern «Einmalige Ausführung» on a real engine - the same scenarios for C7 and C8. All
  * scenarios start with the message (name = process id); the `businessKey` is the scenario name.
  */
abstract class UniqueExecutionSimulation extends CompanySimulation:

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
    incidentScenario(
      `UniqueExecution invalid In`,
      "customerName must not be empty."
    ).startWithMsg
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

  // the businessKey of the running instance is the scenario name; it waits until the InitWorker
  // set `alreadyRunning = false` - otherwise the InitWorker may overwrite the `true`
  private lazy val `Start Again ME` =
    StartAgainME.example
      .withBusinessKey("UniqueExecution started twice")
      .waitFor("alreadyRunning", false)

  private lazy val `Edit Request UT` =
    EditRequestUT.example

end UniqueExecutionSimulation
