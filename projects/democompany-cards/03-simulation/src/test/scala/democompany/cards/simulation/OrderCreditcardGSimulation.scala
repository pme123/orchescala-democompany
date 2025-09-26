package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import zio.ZLayer

// ./helper.scala deploy OrderCreditcardGSimulation
// simulation/test
// simulation/testOnly *OrderCreditcardGSimulation
class OrderCreditcardGSimulation extends OrderCreditcardSimulation, CompanyGSimulation