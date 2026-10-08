package democompany.customer
package worker.appointments

import democompany.customer.domain.appointments.AppointmentToConfirm.*
import democompany.customer.worker.appointments.AppointmentsStore.reservations

/** For the page «Termin bestätigen»: the reservation of the token in the link of the advisor's mail
  * and the open task of its process (found in Operaton by the business key = token). Only for
  * advisors - the role `kundenberater`.
  */
class AppointmentToConfirmWorker extends CompanyPersistenceWorkerDsl[In, Out]:

  lazy val customTask = example

  override def requiredRoles: Set[String] = Set("kundenberater")

  override def runWorkZIO(in: In): RunWorkZIOOutput[Out] =
    for
      found  <- query(reservations, Map("token" -> in.token), limit = 1)
      stored <- ZIO
                  .fromOption(found.headOption)
                  .orElseFail(WorkerError.CustomError.refused(404, "No appointment for this link - it may have expired."))
      taskId <- ConfirmTasks.openTask(in.token)
      r       = stored.entity
    yield Out(r.appointment, r.contact, r.remark, r.status.toString, taskId)

end AppointmentToConfirmWorker

/** The open user task «Termin bestätigen» of a process - over the REST API of Operaton. */
object ConfirmTasks:
  val taskDefinitionKey = "ConfirmAppointmentTask"

  private lazy val client = java.net.http.HttpClient.newHttpClient()

  def openTask(businessKey: String): zio.IO[WorkerError.CustomError, Option[String]] =
    val uri     = java.net.URI.create(
      s"${AppointmentRules.restUrl}/task?processInstanceBusinessKey=${java.net.URLEncoder.encode(businessKey, "UTF-8")}" +
        s"&taskDefinitionKey=$taskDefinitionKey"
    )
    val request = java.net.http.HttpRequest.newBuilder(uri).GET().build()
    ZIO
      .attemptBlocking(client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()))
      .mapError(e => WorkerError.CustomError(s"The tasks cannot be read: ${e.getMessage}"))
      .flatMap: response =>
        if response.statusCode != 200 then
          ZIO.fail(WorkerError.CustomError(s"The tasks answered ${response.statusCode}"))
        else
          ZIO
            .fromEither(io.circe.parser.parse(response.body).flatMap(_.hcursor.downN(0).downField("id").as[Option[String]]))
            .orElse(ZIO.none)
  end openTask
end ConfirmTasks
