package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.AssignCustomer.*
import democompany.customer.domain.bookAppointment.v1.schema.{Contact, CustomerStatus}

//sbt worker/testOnly *AssignCustomerWorkerTest
class AssignCustomerWorkerTest extends munit.FunSuite:

  lazy val worker = AssignCustomerWorker()

  private def contact(no: Option[String], email: String = "someone@example.ch") =
    In(Contact.example.copy(customerNo = no, email = email))

  test("a known customer number - customer"):
    assertEquals(worker.runWork(contact(Some("100200"))), Right(Out(CustomerStatus.customer, Some("100200"), Some("Anna Muster"))))

  test("no number, but a known e-mail - customer"):
    assertEquals(worker.runWork(contact(None, "Peter.Muster@example.ch")).map(_.clientId), Right(Some("100300")))

  test("unknown - prospect"):
    assertEquals(worker.runWork(contact(Some("999999"))), Right(Out(CustomerStatus.prospect, None, None)))

end AssignCustomerWorkerTest
