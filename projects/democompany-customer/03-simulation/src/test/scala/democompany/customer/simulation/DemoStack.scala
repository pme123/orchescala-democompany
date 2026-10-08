package democompany.customer
package simulation

import democompany.customer.domain.appointments.{FreeSlots, ReserveSlot}
import democompany.customer.domain.bookAppointment.v1.schema.{Appointment, Channel, Contact, Topic}
import io.circe.parser.decode
import io.circe.syntax.*

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

/** What a simulation needs before a start - over the public gateway, like the page «Termin
  * buchen»: a free slot and its reservation. `GATEWAY_URL` (default the demo stack).
  */
object DemoStack:
  lazy val gatewayUrl: String = sys.env.getOrElse("GATEWAY_URL", "http://localhost:8889").stripSuffix("/")

  case class Reserved(reservationId: String, token: String, appointment: Appointment, contact: Contact)

  private lazy val client = HttpClient.newHttpClient()

  /** One reservation per topic and channel - each on another slot (they run at the same time). */
  def reserve(wanted: Seq[(Topic, Channel)]): Seq[Reserved] =
    wanted.zipWithIndex.map: (tc, i) =>
      val (topic, channel) = tc
      val slots            = post[FreeSlots.Out](FreeSlots.topicName, FreeSlots.In(topic, channel, None, 14).asJson).slots
      val slot             = slots.lift(i * 3).orElse(slots.headOption)
        .getOrElse(throw IllegalStateException(s"No free slot for $topic / $channel - is process A run?"))
      val contact          = Contact.example
      val out              = post[ReserveSlot.Out](ReserveSlot.topicName, ReserveSlot.In(slot, contact, None).asJson)
      Reserved(out.reservationId, out.token, slot, contact)

  private def post[Out: io.circe.Decoder](topic: String, body: io.circe.Json): Out =
    val request  = HttpRequest
      .newBuilder(URI.create(s"$gatewayUrl/public/worker/$topic"))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(body.deepMerge(io.circe.Json.obj("_hp" -> "".asJson)).noSpaces))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if response.statusCode != 200 then
      throw IllegalStateException(s"$topic answered ${response.statusCode}: ${response.body.take(200)}")
    decode[Out](response.body).fold(e => throw IllegalStateException(s"$topic: ${e.getMessage}"), identity)
end DemoStack
