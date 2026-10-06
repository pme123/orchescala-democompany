package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.appointments.CalendarSnapshot
import democompany.customer.domain.updateAvailability.v1.SaveCalendarSnapshot.*
import democompany.customer.worker.appointments.AppointmentsStore.calendars

/** Replaces the last calendar state of the advisor. */
class SaveCalendarSnapshotWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    val now = LocalDateTime.now()
    for
      last <- get(calendars, in.advisor.id)
      _    <- save(calendars, CalendarSnapshot(in.advisor.id, in.busy, now), last.map(_.version))
    yield Out(now)
  end runWorkZIO

end SaveCalendarSnapshotWorker
