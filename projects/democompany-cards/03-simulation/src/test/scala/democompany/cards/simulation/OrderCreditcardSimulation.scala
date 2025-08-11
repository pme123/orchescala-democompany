package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.CheckOrderTask
import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import democompany.services.domain.clients.v1.GetClientsClientId

abstract class OrderCreditcardSimulation extends CompanySimulation:

  simulate(
    scenario(`OrderCreditcard`)(
      `Check Order approved UT`
    ),
    scenario(`OrderCreditcard minimal`)(
      `Check Order approved UT minimal`
    ),
    scenario(`OrderCreditcard not approved`)(
      `Check Order NOT approved UT`
    ),
    scenario(`OrderCreditcard not approved no email`)(
      `Check Order NOT approved UT`
    ),
    scenario(`OrderCreditcard mocked`)(
      // TODO remove or add process steps like UserTasks
    )
  )

  override def config =
    super.config
      .withMaxCount(5)
    // .withLogLevel(LogLevel.DEBUG)

  private lazy val `OrderCreditcard` =
    example
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard not approved` =
    example
      .withOut(outExample.copy(processStatus = ProcessStatus.notSucceeded))
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard not approved no email` =
    example
      .withIn(in =>
        inExample.copy(inConfig =
          Some(in.inConfig.getOrElse(InConfig()).copy(
            getClientMock =
              Some(GetClientsClientId.Out.example.copy(email = None))
          ))
        )
      )
      .withOut(outExample.copy(processStatus = ProcessStatus.notSucceeded))
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

  private lazy val `Check Order approved UT` =
    CheckOrderTask.example

  private lazy val `Check Order approved UT minimal` =
    CheckOrderTask.exampleMinimal

  private lazy val `Check Order NOT approved UT` =
    CheckOrderTask.example
      .withOut(CheckOrderTask.Out.example.copy(approved = false))

  private lazy val workers = Seq()

end OrderCreditcardSimulation
