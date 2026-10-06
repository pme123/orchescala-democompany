package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.schema.Advisor
import democompany.customer.worker.appointments.CalendarMock

import java.time.DayOfWeek

//sbt worker/testOnly *ReadCalendarWorkerTest
class ReadCalendarWorkerTest extends munit.FunSuite:

  private val anna   = Advisor("anna.berater", "Anna Berater", "anna.berater@democompany.ch")
  private val monday = LocalDate.of(2026, 10, 19)

  test("calendar mock - busy on workdays only, the lunch break every day"):
    val busy = CalendarMock.busy(anna, monday, 7)
    assertEquals(busy.map(_.start.toLocalDate).distinct.size, 5)
    assert(!busy.exists(b => Set(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)(b.start.getDayOfWeek)))
    assertEquals(busy.count(_.start.toLocalTime == LocalTime.NOON), 5)

  test("calendar mock - the same advisor and day give the same times, sorted"):
    val busy = CalendarMock.busy(anna, monday, 14)
    assertEquals(busy, CalendarMock.busy(anna, monday, 14))
    assertEquals(busy, busy.sortBy(_.start))
    assert(busy.forall(b => b.end.isAfter(b.start)))

end ReadCalendarWorkerTest
