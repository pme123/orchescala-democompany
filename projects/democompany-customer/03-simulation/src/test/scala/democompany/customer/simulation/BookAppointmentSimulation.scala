package democompany.customer
package simulation

import democompany.customer.domain.bookAppointment.v1.BookAppointment.*

// ./helper.scala deploy BookAppointmentSimulation
// simulation/test
// simulation/testOnly *BookAppointmentSimulation
class BookAppointmentSimulation extends CompanyOpSimulation:

  simulate(
    scenario(`BookAppointment`)(
      //TODO remove or add process steps like UserTasks
    ),
    scenario(`BookAppointment minimal`)(
      //TODO remove or add process steps like UserTasks
    )
  )

  override def config =
    super.config
      //.withMaxCount(30)
      //.withLogLevel(LogLevel.DEBUG)

  private lazy val `BookAppointment` =
    example
      .mockServices
      .mockWorkers(workers*)

  private lazy val `BookAppointment minimal` =
    exampleMinimal
      .mockServices
      .mockWorkers(workers*)

  private lazy val workers = Seq()

end BookAppointmentSimulation