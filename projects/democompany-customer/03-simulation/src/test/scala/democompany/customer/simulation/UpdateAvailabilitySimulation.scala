package democompany.customer
package simulation

import democompany.customer.domain.updateAvailability.v1.UpdateAvailability.*

// ./helper.scala deploy UpdateAvailabilitySimulation
// simulation/test
// simulation/testOnly *UpdateAvailabilitySimulation
class UpdateAvailabilitySimulation extends CompanyOpSimulation:

  simulate(
    scenario(`UpdateAvailability`)(
      //TODO remove or add process steps like UserTasks
    ),
    scenario(`UpdateAvailability minimal`)(
      //TODO remove or add process steps like UserTasks
    )
  )

  override def config =
    super.config
      //.withMaxCount(30)
      //.withLogLevel(LogLevel.DEBUG)

  private lazy val `UpdateAvailability` =
    example
      .mockServices
      .mockWorkers(workers*)

  private lazy val `UpdateAvailability minimal` =
    exampleMinimal
      .mockServices
      .mockWorkers(workers*)

  private lazy val workers = Seq()

end UpdateAvailabilitySimulation