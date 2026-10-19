package democompany.customer.domain.appointments

import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Contact}
import democompany.customer.domain.updateAvailability.v1.schema.BusyPeriod

/** The own data of the area appointments (PersistenceWorker) - no interface of a process. */

/** Reserved (until `reservedUntil`, the opt-in), requested (the opt-in is done - held until the
  * decision of the advisor, it does not expire) or booked.
  */
enum ReservationStatus:
  case reserved, requested, booked

object ReservationStatus:
  given ApiSchema[ReservationStatus]  = deriveEnumApiSchema
  given InOutCodec[ReservationStatus] = deriveEnumInOutCodec
end ReservationStatus

/** A slot reserved or booked over the app. The id is `<advisor>-<start>` - so a slot exists
  * once: a second reservation of it fails (see `reserveSlot`).
  */
case class Reservation(
    @description("`<advisor>-<start>`")
    id: String,
    @description("Ein Zufallswert (UUID) - Business Key des Prozesses, in den Links der Mails; wer ihn kennt, darf die Reservierung bestätigen")
    token: String,
    @description("Der Termin")
    appointment: Appointment,
    @description("Kontaktdaten")
    contact: Contact,
    @description("Bemerkung")
    remark: Option[String],
    @description("reserviert (bis zum Opt-in), angefragt (Opt-in bestätigt - bis zum Entscheid des Beraters) oder gebucht")
    status: ReservationStatus,
    @description("Bis wann die Reservierung ohne Opt-in gilt - danach ist der Termin wieder frei; eine angefragte läuft nicht ab")
    reservedUntil: LocalDateTime
)

object Reservation:
  given ApiSchema[Reservation]  = deriveApiSchema
  given InOutCodec[Reservation] = deriveInOutCodec

  def idOf(advisorId: String, start: LocalDateTime): String = s"$advisorId-$start"
end Reservation

/** The busy times of an advisor as Outlook had them at `takenAt` (process updateAvailability). */
case class CalendarSnapshot(
    @description("Berater (Benutzer-ID)")
    advisorId: String,
    @description("Belegte Zeiten")
    busy: Seq[BusyPeriod],
    @description("Stand")
    takenAt: LocalDateTime
)

object CalendarSnapshot:
  given ApiSchema[CalendarSnapshot]  = deriveApiSchema
  given InOutCodec[CalendarSnapshot] = deriveInOutCodec
end CalendarSnapshot
