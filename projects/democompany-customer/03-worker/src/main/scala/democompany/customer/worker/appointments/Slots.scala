package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Topic}
import democompany.customer.domain.updateAvailability.v1.schema.{Advisor, BusyPeriod}
import democompany.customer.domain.bookAppointment.v1.AppointmentRulesDmn

import java.time.{DayOfWeek, LocalDate, LocalDateTime, LocalTime}

/** The free slots - a pure function, so it is tested without engine and database:
  *
  * `frei = Regeln (Zeitfenster, Dauer, Vorlaufzeit, Puffer) − Outlook belegt − reserviert/gebucht`
  *
  * Slots start on a 30 minute grid inside the time window; busy times and reservations count with
  * the buffer on both sides. An expired reservation (not booked) is free again.
  */
object Slots:
  val grid: Int = 30 // minutes

  case class Calendar(advisor: Advisor, busy: Seq[BusyPeriod])

  def free(
      rules: AppointmentRulesDmn.Out,
      topic: Topic,
      channel: Channel,
      calendars: Seq[Calendar],
      reservations: Seq[Reservation],
      from: LocalDate,
      days: Int,
      now: LocalDateTime
  ): Seq[Appointment] =
    val earliest = now.plusHours(rules.leadTimeHours)
    val window   = LocalTime.parse(rules.windowStart) -> LocalTime.parse(rules.windowEnd)
    val taken    = reservations.filter(r => r.status == ReservationStatus.booked || r.reservedUntil.isAfter(now))
    for
      cal   <- calendars
      day   <- (0 until days).map(from.plusDays(_))
      if day.getDayOfWeek != DayOfWeek.SATURDAY && day.getDayOfWeek != DayOfWeek.SUNDAY
      start <- starts(day, window, rules.durationMinutes)
      end    = start.plusMinutes(rules.durationMinutes)
      if !start.isBefore(earliest)
      blocked = cal.busy.map(b => b.start -> b.end) ++
                  taken.filter(_.appointment.advisorId == cal.advisor.id).map(r => r.appointment.start -> r.appointment.end)
      if !blocked.exists((s, e) => overlaps(start, end, s.minusMinutes(rules.bufferMinutes), e.plusMinutes(rules.bufferMinutes)))
    yield Appointment(cal.advisor.id, cal.advisor.name, cal.advisor.email, start, end, topic, channel)
    end for
  end free

  private def starts(day: LocalDate, window: (LocalTime, LocalTime), duration: Int): Seq[LocalDateTime] =
    val (open, close) = window
    Iterator
      .iterate(day.atTime(open))(_.plusMinutes(grid))
      .takeWhile(s => !s.plusMinutes(duration).isAfter(day.atTime(close)))
      .toSeq

  private def overlaps(s1: LocalDateTime, e1: LocalDateTime, s2: LocalDateTime, e2: LocalDateTime) =
    s1.isBefore(e2) && s2.isBefore(e1)
end Slots

/** The appointment rules - evaluated by the engine (Operaton REST), so the deployed DMN counts,
  * the same the process uses. `OPERATON_BASE_URL` as for the engine of the company.
  */
object AppointmentRules:
  lazy val restUrl: String = sys.env.getOrElse("OPERATON_BASE_URL", "http://localhost:9999/engine-rest").stripSuffix("/")
  private lazy val client  = java.net.http.HttpClient.newHttpClient()

  def evaluate(in: AppointmentRulesDmn.In): zio.IO[WorkerError.CustomError, AppointmentRulesDmn.Out] =
    val body = io.circe.Json.obj(
      "variables" -> io.circe.Json.obj(
        "topic"          -> variable(in.topic),
        "channel"        -> variable(in.channel),
        "customerStatus" -> variable(in.customerStatus)
      )
    )
    val request = java.net.http.HttpRequest
      .newBuilder(java.net.URI.create(s"$restUrl/decision-definition/key/${AppointmentRulesDmn.decisionId}/evaluate"))
      .header("Content-Type", "application/json")
      .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body.noSpaces))
      .build()
    ZIO
      .attemptBlocking(client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()))
      .mapError(e => WorkerError.CustomError(s"The appointment rules cannot be evaluated: ${e.getMessage}"))
      .flatMap: response =>
        if response.statusCode != 200 then
          ZIO.fail(WorkerError.CustomError(s"The appointment rules answered ${response.statusCode}: ${response.body.take(300)}"))
        else ZIO.fromEither(result(response.body))
  end evaluate

  private def variable(value: String) = io.circe.Json.obj("value" -> io.circe.Json.fromString(value), "type" -> io.circe.Json.fromString("String"))

  /** `[{"durationMinutes": {"type": "Integer", "value": 90}, …}]` - the first (FIRST) rule. */
  def result(json: String): Either[WorkerError.CustomError, AppointmentRulesDmn.Out] =
    io.circe.parser
      .parse(json)
      .flatMap(_.hcursor.downN(0).as[Map[String, io.circe.Json]])
      .flatMap: row =>
        io.circe.Json.fromFields(row.view.mapValues(_.hcursor.downField("value").focus.getOrElse(io.circe.Json.Null)))
          .as[AppointmentRulesDmn.Out]
      .left.map(e => WorkerError.CustomError(s"The appointment rules gave no result: ${e.getMessage}"))
end AppointmentRules
