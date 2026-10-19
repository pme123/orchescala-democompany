package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.AssignCustomer.*
import democompany.customer.domain.bookAppointment.v1.schema.{Contact, CustomerStatus}

//sbt worker/testOnly *AssignCustomerWorkerTest
// It uses the store - holding the reservation is checked in ReservationsTest and by the simulation.
class AssignCustomerWorkerTest extends munit.FunSuite:

  private def contact(no: Option[String], email: String = "someone@example.ch") =
    In.example.copy(contact = Contact.example.copy(customerNo = no, email = email))

  test("a known customer number - customer"):
    assertEquals(AssignCustomerWorker.assign(contact(Some("100200"))), Out(CustomerStatus.customer, Some("100200"), Some("Anna Muster")))

  test("no number, but a known e-mail - customer"):
    assertEquals(AssignCustomerWorker.assign(contact(None, "Peter.Muster@example.ch")).clientId, Some("100300"))

  test("unknown - prospect"):
    assertEquals(AssignCustomerWorker.assign(contact(Some("999999"))), Out(CustomerStatus.prospect, None, None))

end AssignCustomerWorkerTest
