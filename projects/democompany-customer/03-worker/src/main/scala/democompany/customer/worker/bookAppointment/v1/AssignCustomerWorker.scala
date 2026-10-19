package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.bookAppointment.v1.AssignCustomer.*
import democompany.customer.domain.bookAppointment.v1.schema.CustomerStatus
import democompany.customer.worker.appointments.AppointmentsStore.reservations
import democompany.customer.worker.appointments.Reservations

/** Finds the customer by the customer number or the e-mail - here a mock of the customer master
  * data; at a bank the core banking system (e.g. Finnova over its API gateway). And holds the
  * reservation: the opt-in is done, so the slot stays taken until the advisor decides - it does
  * not expire after the 30 minutes of the opt-in.
  */
class AssignCustomerWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      stored    <- getExisting(reservations, in.reservationId)
      requested <- ZIO.fromEither(Reservations.requested(stored.entity, in.token)).mapError(WorkerError.CustomError(_))
      _         <- save(reservations, requested, Some(stored.version))
    yield AssignCustomerWorker.assign(in)

end AssignCustomerWorker

object AssignCustomerWorker:
  case class Customer(no: String, name: String, email: String)
  val customers: Seq[Customer] = Seq(
    Customer("100200", "Anna Muster", "anna.muster@example.ch"),
    Customer("100300", "Peter Muster", "peter.muster@example.ch")
  )

  /** By the customer number, else by the e-mail - else a prospect. */
  def assign(in: In): Out =
    val byNo    = in.contact.customerNo.flatMap(no => customers.find(_.no == no.trim))
    val byEmail = customers.find(_.email.equalsIgnoreCase(in.contact.email.trim))
    byNo.orElse(byEmail) match
      case Some(c) => Out(CustomerStatus.customer, Some(c.no), Some(c.name))
      case None    => Out(CustomerStatus.prospect, None, None)
  end assign
end AssignCustomerWorker
