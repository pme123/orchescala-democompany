package democompany.cards
package simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType

// ./helper.scala deploy OrderCreditcardOperatonSimulation
// simulation/test
// simulation/testOnly *OrderCreditcardOperatonSimulation
class OrderCreditcardOperatonSimulation extends OrderCreditcardSimulation, CompanyOperatonSimulation:
  def engineType: EngineType = EngineType.Op

