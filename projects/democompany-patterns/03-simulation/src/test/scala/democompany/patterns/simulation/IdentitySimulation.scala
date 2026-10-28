package democompany.patterns.simulation

import democompany.patterns.domain.identity.v1.*
import democompany.patterns.domain.identity.v1.Identity.*

/** The patterns «Identität übernehmen» and «Identität zurücksetzen» on a real engine - the same
  * scenarios for C7 and C8.
  */
abstract class IdentitySimulation extends CompanySimulation:

  simulate(
    // 1. «Antrag freigeben» is completed by another identity (`actor`) - the process takes it over
    //    for the next check, «Antrag abschliessen» is completed by the starter (`admin`) and the
    //    reset sets the identity of the start again: CheckIdentity sees `actor`, then `admin`.
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

  // CheckIdentity saw `actor` after the take-over and `admin` after the reset. And all three
  // identities are there at the end: the actor of the last user task, the one before the take-over
  // and the one set again by the reset.
  // With checks like these the simulation checks only them - not the Out of `example`.
  private lazy val `Identity approved` =
    example
      .isEquals(
        "checkedUsernames",
        Seq(testActorIdentityCorrelation.username, testIdentityCorrelation.username)
      )
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
    ApproveRequestUT.example.actingAs(testActorIdentityCorrelation)

  private lazy val `Close Request UT` =
    CloseRequestUT.example

end IdentitySimulation
