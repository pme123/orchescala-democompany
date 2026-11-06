package democompany.patterns.simulation

import democompany.patterns.domain.cancelBySignal.v1.*
import democompany.patterns.domain.cancelBySignal.v1.CancelBySignal.*

/** The pattern «Abbruch per Signal» on a real engine - the same scenarios for C7 and C8. The signal
  * is `cancel-<process instance>`; Orchescala puts in the id of the scenario's instance.
  */
abstract class CancelBySignalSimulation extends CompanySimulation:

  simulate(
    // 1. no signal: «Antrag bearbeiten» is done, the process ends with processStatus = succeeded
    scenario(`CancelBySignal succeeded`)(
      `Edit Request UT`
    ),
    // 2. the cancel signal comes while the process waits at «Antrag bearbeiten»: the event sub
    //    process «Abbruch» interrupts it and ends with processStatus = canceled
    scenario(`CancelBySignal canceled`)(
      `Cancel SE`
    ),
    // 3. the process output is mocked: ends on the path `output-mocked` of «Init Process»
    scenario(`CancelBySignal mocked`)
  )

  // like the other pattern simulations: the start does not ask a worker app to validate the In -
  // the InitWorker in the engine does
  override def config =
    super.config
      .validateProcess(false)

  private lazy val `CancelBySignal succeeded` =
    example

  private lazy val `CancelBySignal canceled` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.canceled))

  private lazy val `CancelBySignal mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

  // waits until the InitWorker is through (`waitForSignal = true`) - then the process waits at the user task
  private lazy val `Cancel SE` =
    CancelSE.example

  private lazy val `Edit Request UT` =
    EditRequestUT.example

end CancelBySignalSimulation
