package democompany.customer
package worker.updateAvailability.v1

import democompany.customer.domain.appointments.CalendarSnapshot
import democompany.customer.domain.updateAvailability.v1.SaveCalendarSnapshot.*
import democompany.customer.worker.appointments.AppointmentsStore.calendars

/** Replaces the last calendar state of the advisor - the newest wins: two runs at the same time
  * (the timer and a start by hand) do not fail on the version, the later one is read again and
  * saved.
  */
class SaveCalendarSnapshotWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    val now = LocalDateTime.now()
    def saveLatest(tries: Int): zio.IO[WorkerError.CustomError, Unit] =
      (for
        last <- get(calendars, in.advisor.id)
        _    <- save(calendars, CalendarSnapshot(in.advisor.id, in.busy, now), last.map(_.version))
      yield ()).catchSome:
        case e if tries > 1 && e.errorMsg.contains("changed in the meantime") => saveLatest(tries - 1)
    saveLatest(tries = 3).as(Out(now))
  end runWorkZIO

end SaveCalendarSnapshotWorker
