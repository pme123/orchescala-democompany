package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.*
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
      `Check Order NOT approved UT`,
      `Receive Email with missing information ME`
    ),
    scenario(`OrderCreditcard not approved not received email`)(
      `Check Order NOT approved UT`,
    // `Email not received TE` //  not supported yet - you must do it with config timer 
    ),
    scenario(`OrderCreditcard not approved no email`)(
      `Check Order NOT approved UT`,
      `Call Client UT`
    ),
    scenario(`OrderCreditcard cancel Order`)(
      `Cancel Order Signal`
        .waitFor("readyToCheckOrder")
    ),
    scenario(`OrderCreditcard mocked`)
  )

  override def config =
    super.config
      .withMaxCount(10)
    // .withLogLevel(LogLevel.DEBUG)

  private lazy val `OrderCreditcard` =
    example
      .mockServices
      .mockWorkers(workers*)
  
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
              Some(GetClientsClientId.Out.example.copy(email = None))
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

  private lazy val `OrderCreditcard cancel Order` =
    `OrderCreditcard`
      .withOut(Out.example.copy(processStatus = ProcessStatus.canceled))
  
  private lazy val `Check Order approved UT` =
    CheckOrderTask.example

  private lazy val `Check Order approved UT minimal` =
    CheckOrderTask.exampleMinimal

  private lazy val `Check Order NOT approved UT` =
    CheckOrderTask.example
      .withOut(CheckOrderTask.Out.example.copy(approved = false))
  private lazy val `Call Client UT` =
    CallClientTask.example
     // .withOut(CheckOrderTask.Out.example.copy(approved = false))

  private lazy val `Receive Email with missing information ME` =
    ReceiveEmailME.example//.withBusinessKey(SignalEvent.Dynamic_ProcessInstance)
  
  private lazy val `Cancel Order Signal` =
    CancelOrderSignal.example
    
  private lazy val `Email not received TE` =
    ReceiveEmailTimer.example
   
  private lazy val workers = Seq()

end OrderCreditcardSimulation
