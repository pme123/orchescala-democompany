package democompany.cards
package simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType

// ./helper.scala deploy OrderCreditcardC7Simulation
// simulation/test
// simulation/testOnly *OrderCreditcardC7Simulation
class OrderCreditcardC7Simulation extends OrderCreditcardSimulation, CompanyC7Simulation:
  override def engineType: EngineType = EngineType.C7
