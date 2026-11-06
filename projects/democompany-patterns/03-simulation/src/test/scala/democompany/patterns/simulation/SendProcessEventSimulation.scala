package democompany.patterns.simulation

import democompany.patterns.domain.processEvent.v1.ProcessEvent
import democompany.patterns.domain.sendProcessEvent.v1.SendProcessEvent.*

/** The pattern «Prozess-Event senden» on a real engine - the same scenarios for C7 and C8. */
abstract class SendProcessEventSimulation extends CompanySimulation:

  simulate(
    // 1. reports started and finished to the event process - it ends with processStatus = succeeded;
    //    the services are mocked: «Event melden» (PostEvent) in the event process is mocked only if
    //    `_servicesMocked` reaches it. A call activity whose process is missing (or fails) would stop
    //    it with an incident
    scenario(`SendProcessEvent succeeded`),
    // 2. the process output is mocked: ends on the path `output-mocked` of «Init Process» - no event
    scenario(`SendProcessEvent mocked`),
    // 3. the event process on its own: reports the event (mocked) and ends with succeeded
    scenario(`ProcessEvent reported`)
  )

  // the event process has no InitWorker - the start must not ask the worker app to validate its In
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `SendProcessEvent succeeded` =
    example
      .mockServices

  private lazy val `SendProcessEvent mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

  private lazy val `ProcessEvent reported` =
    ProcessEvent.example
      .mockServices

end SendProcessEventSimulation
