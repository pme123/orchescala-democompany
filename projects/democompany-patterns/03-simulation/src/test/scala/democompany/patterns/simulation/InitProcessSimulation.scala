package democompany.patterns.simulation

import democompany.patterns.domain.initProcess.v1.InitProcess.*

/** The pattern «Init Process» on a real engine - the same scenarios for C7 and C8. */
abstract class InitProcessSimulation extends CompanySimulation:

  simulate(
    // 1. normal run: ends with processStatus = succeeded - the defaults of the InConfig are set
    scenario(`InitProcess`),
    // 2. the process output is mocked: ends on the path `output-mocked`
    scenario(`InitProcess mocked`),
    // 3. invalid In: the InitWorker reports validation-failed (not handled -> incident)
    incidentScenario(
      `InitProcess invalid In`,
      "customerName must not be empty."
    )
  )

  // The start does not ask the worker app to validate the In (otherwise an invalid In is refused
  // before the process starts) - so all scenarios go through the InitWorker in the engine.
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `InitProcess` =
    example

  private lazy val `InitProcess mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

  private lazy val `InitProcess invalid In` =
    example
      .withIn(In.example.copy(customerName = " "))

end InitProcessSimulation
