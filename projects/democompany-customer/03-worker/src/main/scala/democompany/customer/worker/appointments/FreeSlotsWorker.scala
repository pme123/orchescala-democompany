package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.FreeSlots.*
import democompany.customer.domain.bookAppointment.v1.AppointmentRulesDmn
import democompany.customer.worker.appointments.AppointmentsStore.{calendars, reservations}

/** The free slots for the page «Termin buchen» - see [[Slots]]. An advisor without calendar
  * snapshot (process updateAvailability has not run yet) is not offered: his calendar is unknown.
  */
class FreeSlotsWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    val now  = LocalDateTime.now()
    val from = in.from.getOrElse(now.toLocalDate)
    val days = in.days.max(1).min(31)
    for
      rules  <- AppointmentRules.evaluate(AppointmentRulesDmn.In(in.topic.toString, in.channel.toString, "customer"))
      known  <- ZIO.foreach(Advisors.all)(a => get(calendars, a.id).map(_.map(s => Slots.Calendar(a, s.entity.busy))))
      cals    = known.flatten
      taken  <- ZIO.foreach(cals): c =>
                  ZIO.foreach((0 until days).map(from.plusDays(_))): day =>
                    query(reservations, Map("advisorId" -> c.advisor.id, "date" -> day.toString))
    yield Out(
      Slots
        .free(rules, in.topic, in.channel, cals, taken.flatten.flatten.map(_.entity), from, days, now)
        .sortBy(_.start)
    )
    end for
  end runWorkZIO

end FreeSlotsWorker
