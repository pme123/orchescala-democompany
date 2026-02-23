package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.*
import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import democompany.services.domain.clients.v1.schema.Client
import orchescala.engine.domain.EngineType

abstract class OrderCreditcardSimulation extends CompanySimulation:
  // only needed for an end event that throws an error. see documentation
  protected def engineType: EngineType

  simulate(
    only.scenario(`OrderCreditcard`)(
      `Check Order approved UT`
    ),
    scenario(`OrderCreditcard by message`.startWithMsg)( // only works with C7!
      `Check Order approved UT`
    ),
    scenario(`OrderCreditcard minimal`)(
      `Check Order approved UT minimal`
    ),
    scenario(`OrderCreditcard not approved`)(
      `Check Order NOT approved UT`,
      `Receive Email with missing information ME`
    ),
    scenario(`OrderCreditcard not approved not received email`)(
      `Check Order NOT approved UT`
      // `Email not received TE` //  not supported yet - you must do it with config timer
    ),
    scenario(`OrderCreditcard not approved no email`)(
      `Check Order NOT approved UT no email`,
      `Call Client UT`
    ),
    scenario(`OrderCreditcard cancel Order`)(
      `Cancel Order Signal`
        .waitFor("readyToCheckOrder")
    ),
    scenario(`OrderCreditcard mocked`),
    incidentScenario(
      `OrderCreditcard NOT handled error`,
      "444: Service Error: 444\nErrorMsg: Mocked Error:"
    ),
    // because handled differently
    if engineType == EngineType.C8 then
      incidentScenario( // thrown in the end event
        `OrderCreditcard handled error`,
        "Expected to throw an error event with the code 'client-not-found', but it was not caught."
      )
    else
      scenario(`OrderCreditcard handled error`)
  )

  private lazy val `OrderCreditcard` =
    example
      .mockServices
      .mockWorkers(workers*)

  protected lazy val `OrderCreditcard by message` =
    `OrderCreditcard`

  private lazy val `OrderCreditcard not approved` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.notSucceeded))
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard not approved no email` =
    example
      .withIn(in =>
        In.example.copy(inConfig =
          Some(in.inConfig.getOrElse(InConfig()).copy(
            getClientMock =
              Some(MockedServiceResponse.success200(Client.example.copy(email =
                None
              )).withHeader("ETag", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIi....."))
          ))
        )
      )
      .withOut(Out.example.copy(processStatus = ProcessStatus.notSucceeded))
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard not approved not received email` =
    `OrderCreditcard`
      .withIn(in =>
        In.example.copy(inConfig =
          Some(in.inConfig.getOrElse(InConfig()).copy(
            timerNotReceivedEmail = "PT0S"
          ))
        )
      )
      .withOut(Out.example.copy(processStatus = ProcessStatus.notSucceeded))

  private lazy val `OrderCreditcard minimal` =
    exampleMinimal
      .mockServices
      .mockWorkers(workers*)

  private lazy val `OrderCreditcard mocked` =
    example
      .withOut(Out.example.copy(processStatus = ProcessStatus.`output-mocked`))
      .mockWorkers(OrderCreditcard.processName)

  private lazy val `OrderCreditcard handled error`     =
    example
      .withIn(In.example.copy(inConfig =
        Some(InConfig(
          getClientMock =
            Some(MockedServiceResponse.error(404, Json.obj()))
        ))
      ))
      .withOut(
        Out.example.copy(processStatus = ProcessStatus.failed)
      )
  private lazy val `OrderCreditcard NOT handled error` =
    example
      .withIn(
        In.example.copy(inConfig =
          Some(InConfig(
            getClientMock = Some(MockedServiceResponse.error(444))
          ))
        )
      )
  private lazy val `OrderCreditcard cancel Order`      =
    `OrderCreditcard`
      .withOut(Out.example.copy(processStatus = ProcessStatus.canceled))

  protected lazy val `Check Order approved UT` =
    CheckOrderTask.example

  private lazy val `Check Order approved UT minimal` =
    CheckOrderTask.exampleMinimal

  private lazy val `Check Order NOT approved UT` =
    CheckOrderTask.example
      .withOut(CheckOrderTask.Out.example.copy(approved = false))

  private lazy val `Check Order NOT approved UT no email` =
    CheckOrderTask.example
      .withIn(CheckOrderTask.In.example.copy(client = Client.example.copy(email = None)))
      .withOut(CheckOrderTask.Out.example.copy(approved = false))
  private lazy val `Call Client UT`                       =
    CallClientTask.example
      .withIn(CallClientTask.In.example.copy(client = Client.example.copy(email = None)))

  private lazy val `Receive Email with missing information ME` =
    ReceiveEmailME.example // .withBusinessKey(SignalEvent.Dynamic_ProcessInstance)

  private lazy val `Cancel Order Signal` =
    CancelOrderSignal.example

  private lazy val `Email not received TE` =
    ReceiveEmailTimer.example

  private lazy val workers = Seq()

end OrderCreditcardSimulation
