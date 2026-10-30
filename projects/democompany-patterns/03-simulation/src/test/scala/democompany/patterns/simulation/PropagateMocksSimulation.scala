package democompany.patterns.simulation

import democompany.patterns.domain.propagateMocks.v1.*
import democompany.patterns.domain.propagateMocks.v1.PropagateMocks.*

/** The pattern «Mocks und Identität weitergeben» on a real engine - the same scenarios for C7 and
  * C8. The mocks are set on the calling process only - the sub process gets them through the call
  * activity.
  */
abstract class PropagateMocksSimulation extends CompanySimulation:

  simulate(
    // 1. the services are mocked: «Kunde prüfen» in the sub process only succeeds if
    //    `_servicesMocked` and a valid `_identityCorrelation` reach it
    scenario(`PropagateMocks services mocked`),
    // 2. the InitWorker of the sub process is mocked: the sub process ends on `output-mocked`
    scenario(`PropagateMocks sub worker mocked`),
    // 3. invalid In: the InitWorker of the caller reports validation-failed (incident)
    incidentScenario(
      `PropagateMocks invalid In`,
      "customerName must not be empty."
    )
  )

  // The start does not ask the worker app to validate the In (otherwise an invalid In is refused
  // before the process starts) - so all scenarios go through the InitWorker in the engine.
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `PropagateMocks services mocked` =
    example
      .mockServices

  // with a check like `isEquals` the simulation checks only it - not the Out of `example`
  private lazy val `PropagateMocks sub worker mocked` =
    example
      .mockServices
      .mockWorker(PropagateMocksSub.processName)
      .isEquals("subProcessStatus", ProcessStatus.`output-mocked`)
      .isEquals("processStatus", ProcessStatus.succeeded)

  private lazy val `PropagateMocks invalid In` =
    example
      .withIn(In.example.copy(customerName = " "))

end PropagateMocksSimulation
