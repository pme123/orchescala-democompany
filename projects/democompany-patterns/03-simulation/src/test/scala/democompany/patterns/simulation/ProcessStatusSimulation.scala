package democompany.patterns.simulation

import democompany.patterns.domain.processStatus.v1.ProcessStatusExample.*

/** The pattern «Prozess-Status am Ende» on a real engine - the same scenarios for C7 and C8. */
abstract class ProcessStatusSimulation extends CompanySimulation:

  simulate(
    // 1. approved: ends at «succeeded» with processStatus = succeeded
    scenario(`ProcessStatus succeeded`),
    // 2. not approved: ends at «canceled» with processStatus = canceled
    scenario(`ProcessStatus canceled`),
    // 3. the process output is mocked: ends on the path `output-mocked` of «Init Process»
    scenario(`ProcessStatus mocked`)
  )

  private lazy val `ProcessStatus succeeded` =
    example

  private lazy val `ProcessStatus canceled` =
    example
      .withIn(In.example.copy(approved = false))
      .withOut(Out.example.copy(processStatus = ProcessStatus.canceled, approved = false))

  private lazy val `ProcessStatus mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

end ProcessStatusSimulation
