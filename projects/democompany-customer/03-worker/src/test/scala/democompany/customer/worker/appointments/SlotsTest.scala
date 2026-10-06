package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.{Reservation, ReservationStatus}
import democompany.customer.domain.bookAppointment.v1.AppointmentRulesDmn
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Contact, Topic}
import democompany.customer.domain.updateAvailability.v1.schema.{Advisor, BusyPeriod}

//sbt worker/testOnly *SlotsTest
class SlotsTest extends munit.FunSuite:

  private val anna   = Advisor("anna.berater", "Anna Berater", "anna.berater@democompany.ch")
  private val monday = LocalDate.of(2026, 10, 19)
  private val now    = LocalDateTime.of(2026, 10, 16, 18, 0) // Friday evening
  // 60 minutes, 24 hours ahead, 08:00-12:00, 15 minutes buffer
  private val rules  = AppointmentRulesDmn.Out(60, 24, "08:00", "12:00", 15, "advisor", false)

  private def free(busy: Seq[BusyPeriod] = Seq.empty, reservations: Seq[Reservation] = Seq.empty, days: Int = 1, at: LocalDateTime = now) =
    Slots.free(rules, Topic.advice, Channel.branch, Seq(Slots.Calendar(anna, busy)), reservations, monday, days, at)

  private def at(h: Int, m: Int = 0) = monday.atTime(h, m)

  test("a free morning: every 30 minutes, the last one ends with the window"):
    assertEquals(free().map(_.start.toLocalTime.toString), Seq("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00"))
    assert(free().forall(s => s.end == s.start.plusMinutes(60)))

  test("busy in Outlook - blocked with the buffer on both sides"):
    // 09:30-10:00 busy, with 15 minutes buffer 09:15-10:15: 08:00-09:00 fits, 08:30-09:30 not
    val starts = free(Seq(BusyPeriod(at(9, 30), at(10)))).map(_.start.toLocalTime.toString)
    assertEquals(starts, Seq("08:00", "10:30", "11:00"))

  test("reserved or booked - blocked; an expired reservation is free again"):
    def reservation(status: ReservationStatus, until: LocalDateTime) =
      Reservation("x", Appointment.example.copy(advisorId = anna.id, start = at(8), end = at(9)), Contact.example, None, status, until)
    def first(r: Reservation) = free(reservations = Seq(r)).headOption.map(_.start.toLocalTime.toString)
    assertEquals(first(reservation(ReservationStatus.reserved, now.plusMinutes(10))), Some("09:30"))
    assertEquals(first(reservation(ReservationStatus.booked, now.minusDays(1))), Some("09:30"))
    assertEquals(first(reservation(ReservationStatus.reserved, now.minusMinutes(1))), Some("08:00"))

  test("the lead time and no weekends"):
    // on Monday 09:10: only from Tuesday 09:10 on (24 hours) - Saturday and Sunday never
    val slots = free(days = 7, at = at(9, 10))
    assert(slots.forall(_.start.isAfter(at(9, 10).plusHours(24).minusSeconds(1))), slots.map(_.start).toString)
    assert(!slots.exists(s => Set(java.time.DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY)(s.start.getDayOfWeek)))
    assertEquals(slots.head.start, LocalDate.of(2026, 10, 20).atTime(9, 30))

  test("a slot carries the advisor, topic and channel"):
    val slot = free().head
    assertEquals((slot.advisorId, slot.advisorEmail, slot.topic, slot.channel), (anna.id, anna.email, Topic.advice, Channel.branch))

  test("the answer of the engine (DMN evaluate)"):
    val json =
      """[{"durationMinutes":{"type":"Integer","value":90,"valueInfo":{}},"leadTimeHours":{"type":"Integer","value":48},
        |"windowStart":{"type":"String","value":"08:00"},"windowEnd":{"type":"String","value":"17:00"},
        |"bufferMinutes":{"type":"Integer","value":15},"advisorPool":{"type":"String","value":"mortgage"},
        |"confirmationRequired":{"type":"Boolean","value":true}}]""".stripMargin
    assertEquals(AppointmentRules.result(json), Right(AppointmentRulesDmn.Out(90, 48, "08:00", "17:00", 15, "mortgage", true)))
    assert(AppointmentRules.result("[]").isLeft)

end SlotsTest
