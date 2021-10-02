package vandebron.http.serialization
import spray.json.DefaultJsonProtocol
import vandebron.service.QueueServiceActor.ActionPerformed
import vandebron.service.{CustomerDetailsResponse, HttpRequests, Requests}

object JsonFormats  {
  import DefaultJsonProtocol._

  implicit val httpRequestsDtoFormat = jsonFormat1(HttpRequests)
  implicit val httpRequestsDtosFormat = jsonFormat1(Requests)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
  implicit val customerDetailsMadJsonFormat = jsonFormat2(CustomerDetailsResponse)
}
