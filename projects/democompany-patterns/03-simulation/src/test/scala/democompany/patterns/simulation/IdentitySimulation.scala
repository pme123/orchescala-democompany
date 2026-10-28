package democompany.patterns.simulation

import democompany.patterns.domain.identity.v1.*
import democompany.patterns.domain.identity.v1.Identity.*

/** The patterns «Identität übernehmen» and «Identität zurücksetzen» on a real engine - the same
  * scenarios for C7 and C8.
  */
abstract class IdentitySimulation extends CompanySimulation:

  simulate(
    // 1. the user task is completed through the engine service with the test identity - the
    //    process takes it over for «Antrag abschliessen» and then sets the identity of the start again
    scenario(`Identity approved`)(
      `Approve Request UT`,
      `Close Request UT`
    ),
    // 2. the process output is mocked: ends on the path `output-mocked` - no user task
    scenario(`Identity mocked`),
    // 3. invalid In: the InitWorker reports validation-failed (not handled -> incident)
    incidentScenario(
      `Identity invalid In`,
      "customerName must not be empty."
    )
  )

  // The start does not ask the worker app to validate the In (otherwise an invalid In is refused
  // before the process starts) - so all scenarios go through the InitWorker in the engine.
  override def config =
    super.config
      .validateProcess(false)

  // all three identities are there at the end: the actor of the last user task, the one before
  // the take-over and the one set again by the reset.
  private lazy val `Identity approved` =
    example
      .exists("_identityCorrelationOfActor")
      .exists("_identityCorrelationBefore")
      .exists("_identityCorrelation")

  private lazy val `Identity mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

  private lazy val `Identity invalid In` =
    example
      .withIn(In.example.copy(customerName = " "))

  private lazy val `Approve Request UT` =
    ApproveRequestUT.example

  private lazy val `Close Request UT` =
    CloseRequestUT.example

end IdentitySimulation
