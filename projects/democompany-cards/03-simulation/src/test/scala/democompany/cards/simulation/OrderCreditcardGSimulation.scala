package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType
import zio.ZLayer

// ./helper.scala deploy OrderCreditcardGSimulation
// simulation/test
// simulation/testOnly *OrderCreditcardGSimulation
class OrderCreditcardGSimulation extends OrderCreditcardSimulation, CompanyGSimulation:
  // only needed for an end event that throws an error. see documentation
  protected def engineType: EngineType = EngineType.C7