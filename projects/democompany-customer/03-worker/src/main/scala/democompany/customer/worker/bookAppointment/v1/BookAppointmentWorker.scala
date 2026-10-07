package democompany.customer
package worker.bookAppointment.v1

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.bookAppointment.v1.BookAppointment.*
import democompany.customer.worker.appointments.{AppointmentTexts, AppointmentsConfig, AppointmentsStore}
import democompany.orchescala.worker.CompanyStore

/** Checks the reservation and sets the flat variables for the mails and the appointment rules.
  *
  * The process can be started without login (from the homepage) - so it only starts for a slot
  * that was reserved before (`reserveSlot`), with the same slot and e-mail, and not expired.
  */
class BookAppointmentWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:

  lazy val inOutExample = example

  /** Before the start - the gateway calls it synchronously (also for a start without login), so an
    * invalid reservation is a 400 and no process instance is created. `validate` is synchronous -
    * the reservation is read blocking (one row by its id).
    */
  override def validate(in: In): Either[WorkerError.ValidatorError, In] =
    val stored = zio.Unsafe.unsafe(implicit u =>
      zio.Runtime.default.unsafe.run(CompanyStore.store.get(AppointmentsStore.reservations, in.reservationId).either).getOrThrow()
    )
    stored match
      case Left(e)  => Left(WorkerError.ValidatorError(s"The reservation cannot be read: ${e.message}"))
      case Right(r) =>
        BookAppointmentWorker.verify(in, r.map(_.entity), LocalDateTime.now())
          .left.map(e => WorkerError.ValidatorError(e.errorMsg))
          .map(_ => in)
  end validate

  /** In the process again - the reservation may have expired in between (e.g. a retry). */
  override protected def customInitZIO(in: In): InitProcessZIOOutput[InitIn] =
    for
      stored <- CompanyStore.store
                  .get(AppointmentsStore.reservations, in.reservationId)
                  .mapError(e => WorkerError.InitProcessError(s"The reservation cannot be read: ${e.message}"))
      _      <- ZIO.fromEither(BookAppointmentWorker.verify(in, stored.map(_.entity), LocalDateTime.now()))
    yield customInit(in)

  override def customInit(in: In): InitIn =
    InitIn(
      email = in.contact.email,
      customerName = AppointmentTexts.customerName(in.contact),
      advisorEmail = in.appointment.advisorEmail,
      topic = in.appointment.topic.toString,
      channel = in.appointment.channel.toString,
      appointmentText = AppointmentTexts.of(in.appointment),
      verificationLink = AppointmentsConfig.verificationLink(in.token),
      confirmLink = AppointmentsConfig.confirmLink(in.token)
    )

end BookAppointmentWorker

object BookAppointmentWorker:

  /** The process belongs to its reservation: its token, reserved (not booked), not expired, same
    * slot and e-mail.
    */
  def verify(in: In, reservation: Option[Reservation], now: LocalDateTime): Either[WorkerError.InitProcessError, Unit] =
    def fail(why: String) = Left(WorkerError.InitProcessError(s"No valid reservation ${in.reservationId}: $why"))
    reservation match
      case None                                                       => fail("unknown")
      case Some(r) if r.token != in.token                             => fail("another token")
      case Some(r) if r.status != ReservationStatus.reserved          => fail("already booked")
      case Some(r) if r.reservedUntil.isBefore(now)                   => fail("expired - please choose the slot again")
      case Some(r) if r.appointment != in.appointment                 => fail("another slot")
      case Some(r) if !r.contact.email.equalsIgnoreCase(in.contact.email) => fail("another e-mail")
      case Some(_)                                                    => Right(())
end BookAppointmentWorker
