package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*

abstract class OrderCreditcardSimulation extends CompanySimulation:

  simulate(
    scenario(`OrderCreditcard`)(
      //TODO remove or add process steps like UserTasks
    ),
    scenario(`OrderCreditcard minimal`)(
      //TODO remove or add process steps like UserTasks
    ),
    scenario(`OrderCreditcard mocked`)(
      //TODO remove or add process steps like UserTasks
    )
  )

  override def config =
    super.config
      //.withMaxCount(30)
      //.withLogLevel(LogLevel.DEBUG)

  private lazy val `OrderCreditcard` =
    example
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard minimal` =
    exampleMinimal
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard mocked` =
    example
      .withOut(outExample.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(OrderCreditcard.processName)
      
  private lazy val workers = Seq()

end OrderCreditcardSimulation