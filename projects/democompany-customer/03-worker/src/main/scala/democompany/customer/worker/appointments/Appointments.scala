package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{CalendarSnapshot, Reservation}
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Contact, Topic}
import democompany.customer.domain.updateAvailability.v1.schema.{Advisor, BusyPeriod}
import orchescala.worker.persistence.EntityDef

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, LocalDate, LocalDateTime, LocalTime, ZoneId, ZoneOffset}
import java.util.Locale

/** Settings of the area - per environment from the env. */
object AppointmentsConfig:
  /** Where the app is - the links in the mails point there. */
  lazy val appBaseUrl: String =
    sys.env.getOrElse("APP_BASE_URL", "http://localhost:8888/app/democompany-customer").stripSuffix("/")
  lazy val zone: ZoneId = ZoneId.of(sys.env.getOrElse("APP_TIME_ZONE", "Europe/Zurich"))

  /** With the token of the reservation - unguessable, so only who got the mail can use the link. */
  def verificationLink(token: String): String =
    s"$appBaseUrl/appointments/verified?token=$token"
  def confirmLink(token: String): String =
    s"$appBaseUrl/appointments/confirm?token=$token"
end AppointmentsConfig

/** The tables of the area (PersistenceWorker). */
object AppointmentsStore:
  /** By advisor, day and status - what `freeSlots` needs; by token - for the page «Termin bestätigen». */
  val reservations: EntityDef[Reservation] = EntityDef[Reservation](
    "reservation",
    _.id,
    r =>
      Map(
        "advisorId" -> r.appointment.advisorId,
        "date"      -> r.appointment.start.toLocalDate.toString,
        "status"    -> r.status.toString,
        "token"     -> r.token
      )
  )
  /** One per advisor - the last state of his calendar. */
  val calendars: EntityDef[CalendarSnapshot] = EntityDef[CalendarSnapshot]("calendar_snapshot", _.advisorId)
end AppointmentsStore

/** The advisors - from the configuration (`ADVISORS`: a JSON list of `{id, name, email}`), later from
  * the IdP (Keycloak admin API, Entra over Graph) behind the same function.
  */
object Advisors:
  val demo: Seq[Advisor] = Seq(
    Advisor("anna.berater", "Anna Berater", "anna.berater@democompany.ch"),
    Advisor("marco.berater", "Marco Berater", "marco.berater@democompany.ch")
  )

  lazy val all: Seq[Advisor] =
    sys.env.get("ADVISORS").filter(_.trim.nonEmpty) match
      case None       => demo
      case Some(json) =>
        io.circe.parser.decode[Seq[Advisor]](json).fold(
          e => throw IllegalArgumentException(s"ADVISORS is no JSON list of advisors: ${e.getMessage}"),
          identity
        )

  def find(id: String): Option[Advisor] = all.find(_.id == id)
end Advisors

/** Stands in for Outlook (Microsoft Graph `getSchedule`): busy times that look like a real
  * calendar - the lunch break and per day one or two meetings, always the same for an advisor and a
  * day, so the free slots do not jump between two runs.
  */
object CalendarMock:

  def busy(advisor: Advisor, from: LocalDate, days: Int): Seq[BusyPeriod] =
    (0 until days)
      .map(from.plusDays(_))
      .filterNot(d => d.getDayOfWeek == DayOfWeek.SATURDAY || d.getDayOfWeek == DayOfWeek.SUNDAY)
      .flatMap: day =>
        val seed     = math.abs(s"${advisor.id}-$day".hashCode)
        val lunch    = period(day, LocalTime.of(12, 0), 60)
        val meetings = Seq(LocalTime.of(9, 0), LocalTime.of(10, 30), LocalTime.of(14, 0), LocalTime.of(15, 30))
        val first    = period(day, meetings(seed % 4), 60 + 30 * (seed % 2))
        val second   = Option.when(seed % 3 == 0)(period(day, LocalTime.of(16, 30), 60))
        Seq(first, lunch) ++ second
      .sortBy(_.start)

  private def period(day: LocalDate, start: LocalTime, minutes: Int) =
    val begin = day.atTime(start)
    BusyPeriod(begin, begin.plusMinutes(minutes))
end CalendarMock

/** How a slot reads in the mails and the invitation. */
object AppointmentTexts:
  private val date = DateTimeFormatter.ofPattern("EE dd.MM.yyyy, HH:mm", Locale.GERMAN)
  private val time = DateTimeFormatter.ofPattern("HH:mm")

  def topic(t: Topic): String = t match
    case Topic.advice     => "Beratung"
    case Topic.mortgage   => "Hypothek"
    case Topic.pension    => "Vorsorge"
    case Topic.investment => "Anlage"

  def channel(c: Channel): String = c match
    case Channel.branch => "Filiale"
    case Channel.video  => "Video"
    case Channel.phone  => "Telefon"

  /** `Mo 20.10.2026, 09:00-10:30, Hypothek, Filiale` */
  def of(a: Appointment): String =
    s"${a.start.format(date).replaceFirst("^(\\p{L}+)\\.", "$1")}-${a.end.format(time)}, ${topic(a.topic)}, ${channel(a.channel)}"

  def customerName(c: Contact): String = s"${c.firstName} ${c.lastName}"
end AppointmentTexts

/** The invitation (iCalendar, `METHOD:REQUEST`) - Outlook shows it as an appointment to accept. */
object Invitation:
  private val utc = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

  def ics(reservationId: String, a: Appointment, c: Contact, now: LocalDateTime): String =
    def at(t: LocalDateTime) = t.atZone(AppointmentsConfig.zone).withZoneSameInstant(ZoneOffset.UTC).format(utc)
    def text(s: String)      = s.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n")
    Seq(
      "BEGIN:VCALENDAR",
      "PRODID:-//democompany//appointments//DE",
      "VERSION:2.0",
      "METHOD:REQUEST",
      "BEGIN:VEVENT",
      s"UID:$reservationId@democompany.ch",
      s"DTSTAMP:${at(now)}",
      s"DTSTART:${at(a.start)}",
      s"DTEND:${at(a.end)}",
      s"SUMMARY:${text(s"${AppointmentTexts.topic(a.topic)} - Demo Company")}",
      s"LOCATION:${text(AppointmentTexts.channel(a.channel))}",
      s"DESCRIPTION:${text(s"Termin mit ${a.advisorName}")}",
      s"ORGANIZER;CN=${text(a.advisorName)}:mailto:${a.advisorEmail}",
      s"ATTENDEE;CN=${text(AppointmentTexts.customerName(c))};RSVP=TRUE:mailto:${c.email}",
      s"ATTENDEE;CN=${text(a.advisorName)}:mailto:${a.advisorEmail}",
      "END:VEVENT",
      "END:VCALENDAR"
    ).mkString("", "\r\n", "\r\n")
  end ics
end Invitation
