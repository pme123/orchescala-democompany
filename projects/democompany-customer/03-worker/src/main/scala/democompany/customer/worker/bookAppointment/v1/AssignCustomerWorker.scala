package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.AssignCustomer.*
import democompany.customer.domain.bookAppointment.v1.schema.CustomerStatus

/** Finds the customer by the customer number or the e-mail - here a mock of the customer master
  * data; at a bank the core banking system (e.g. Finnova over its API gateway).
  */
class AssignCustomerWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    val byNo    = in.contact.customerNo.flatMap(no => AssignCustomerWorker.customers.find(_.no == no.trim))
    val byEmail = AssignCustomerWorker.customers.find(_.email.equalsIgnoreCase(in.contact.email.trim))
    Right(
      byNo.orElse(byEmail) match
        case Some(c) => Out(CustomerStatus.customer, Some(c.no), Some(c.name))
        case None    => Out(CustomerStatus.prospect, None, None)
    )
  end runWork

end AssignCustomerWorker

object AssignCustomerWorker:
  case class Customer(no: String, name: String, email: String)
  val customers: Seq[Customer] = Seq(
    Customer("100200", "Anna Muster", "anna.muster@example.ch"),
    Customer("100300", "Peter Muster", "peter.muster@example.ch")
  )
