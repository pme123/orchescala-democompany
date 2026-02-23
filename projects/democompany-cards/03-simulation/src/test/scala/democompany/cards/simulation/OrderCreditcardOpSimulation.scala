package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType

// ./helper.scala deploy OrderCreditcardOpSimulation
// simulation/test
// simulation/testOnly *OrderCreditcardOpSimulation
class OrderCreditcardOpSimulation extends OrderCreditcardSimulation, CompanyOpSimulation:
  def engineType: EngineType = EngineType.Op
d