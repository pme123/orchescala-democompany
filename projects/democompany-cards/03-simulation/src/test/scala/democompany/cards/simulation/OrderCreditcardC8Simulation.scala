package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType

// ./helper.scala deploy OrderCreditcardC8Simulation
// simulation/test
// simulation/testOnly *OrderCreditcardC8Simulation
class OrderCreditcardC8Simulation extends OrderCreditcardSimulation, CompanyC8Simulation:
  // only needed for an end event that throws an error. see documentation
  protected def engineType: EngineType = EngineType.C8