package vandebron.http

import akka.actor.typed.scaladsl.AskPattern.{Askable, _}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import vandebron.service.QueueServiceActor.{ActionPerformed, Enqueue, QueryApis}
import vandebron.service.{HttpRequests, QueueServiceActor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QueryRoute(queue: ActorRef[QueueServiceActor.Command])(implicit val system: ActorSystem[_]) extends BaseRoute {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import vandebron.http.serialization.JsonFormats._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("queuing-service.routes.ask-timeout")) //TODO

  def enqueueRequests(requests: HttpRequests): Future[ActionPerformed] =
    queue.ask(Enqueue(requests, _))

  def queryQueue = queue.ask(QueryApis)

  def route: Route = (post & path("query")) (queryRequests)

  private def queryRequests: Route =
    entity(as[HttpRequests]) { req =>
      complete {
        for {
          _ <- enqueueRequests(req)
          response <- queryQueue
        } yield response
      }
    }
}
