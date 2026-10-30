package democompany.patterns.simulation

import democompany.patterns.domain.uniqueExecution.v1.*
import democompany.patterns.domain.uniqueExecution.v1.UniqueExecution.*

/** The pattern «Einmalige Ausführung» on a real engine - the same scenarios for C7 and C8. They
  * start with the message (name = process id); the `businessKey` is the scenario name.
  *
  * No scenario with an invalid In (InitProcessSimulation has it): its instance stays with the
  * incident, and the message of the next run (same businessKey) would land in it - what the pattern
  * is for (409, no process started). Nor can it start directly in C8 - the process has only the
  * message start.
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
    )
  )

  private lazy val `UniqueExecution started` =
    example

  private lazy val `UniqueExecution started twice` =
    example
      .withOut(Out.example.copy(alreadyRunning = true))

  // the businessKey of the running instance is the scenario name; it waits until the InitWorker
  // set `alreadyRunning = false` - otherwise the InitWorker may overwrite the `true`
  private lazy val `Start Again ME` =
    StartAgainME.example
      .withBusinessKey("UniqueExecution started twice")
      .waitFor("alreadyRunning", false)

  private lazy val `Edit Request UT` =
    EditRequestUT.example

end UniqueExecutionSimulation
