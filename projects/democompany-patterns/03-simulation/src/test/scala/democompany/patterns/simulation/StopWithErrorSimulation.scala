package democompany.patterns.simulation

import democompany.patterns.domain.stopWithError.v1.StopWithError.*

/** The pattern «Prozess mit Fehler stoppen» on a real engine - the same scenarios for C7 and C8. */
abstract class StopWithErrorSimulation extends CompanySimulation:

  simulate(
    // 1. not blocked: ends with processStatus = succeeded
    scenario(`StopWithError succeeded`),
    // 2. blocked: stops at the end with an incident - its message names the customer
    incidentScenario(
      `StopWithError blocked`,
      "Kunde Peter Muster ist gesperrt."
    ),
    // 3. limit exceeded: stops at the intermediate event with an incident
    incidentScenario(
      `StopWithError limit exceeded`,
      "Limit von Peter Muster überschritten."
    ),
    // 4. the process output is mocked: ends on the path `output-mocked` of «Init Process»
    scenario(`StopWithError mocked`)
  )

  private lazy val `StopWithError succeeded` =
    example

  private lazy val `StopWithError blocked` =
    example
      .withIn(In.example.copy(blocked = true))

  private lazy val `StopWithError limit exceeded` =
    example
      .withIn(In.example.copy(limitExceeded = true))

  private lazy val `StopWithError mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(processName)

end StopWithErrorSimulation
