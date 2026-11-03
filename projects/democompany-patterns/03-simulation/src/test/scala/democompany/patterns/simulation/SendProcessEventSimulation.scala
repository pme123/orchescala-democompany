package democompany.patterns.simulation

import democompany.patterns.domain.processEvent.v1.ProcessEvent
import democompany.patterns.domain.sendProcessEvent.v1.SendProcessEvent.*

/** The pattern «Prozess-Event senden» on a real engine - the same scenarios for C7 and C8. */
abstract class SendProcessEventSimulation extends CompanySimulation:

  simulate(
    // 1. reports started and finished to the event process - it ends with processStatus = succeeded;
    //    a call activity whose process is missing (or fails) would stop it with an incident
    scenario(`SendProcessEvent succeeded`),
    // 2. the process output is mocked: ends on the path `output-mocked` of «Init Process» - no event
    scenario(`SendProcessEvent mocked`),
    // 3. the event process (the dummy) on its own: takes the event and ends with succeeded
    scenario(`ProcessEvent dummy`)
  )

  // the dummy has no InitWorker - the start must not ask the worker app to validate its In
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `SendProcessEvent succeeded` =
    example

  private lazy val `SendProcessEvent mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

  private lazy val `ProcessEvent dummy` =
    ProcessEvent.example

end SendProcessEventSimulation
