package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.updateAvailability.v1.ReadCalendar.*
import democompany.customer.worker.appointments.CalendarMock

/** The busy times of the advisor from today on - the calendar mock, later Outlook (Graph). */
class ReadCalendarWorker extends CompanyCustomWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWork(in: In): Either[WorkerError.CustomError, Out] =
    Right(Out(CalendarMock.busy(in.advisor, LocalDate.now(), in.daysAhead)))

end ReadCalendarWorker
