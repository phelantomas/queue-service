package vandebron.service

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class ProductApiService(implicit system: ActorSystem[_]) {
  def getProductResponses(uri: String): Future[Map[String, Seq[String]]] = {
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import spray.json.DefaultJsonProtocol._

    val request = HttpRequest(method = HttpMethods.GET, uri = uri)
    val responseFuture = Http().singleRequest(request)
    val response: HttpResponse = Await.result[HttpResponse](responseFuture, 5 seconds)
    Unmarshal(response.entity).to[Map[String, Seq[String]]]
  }
}
