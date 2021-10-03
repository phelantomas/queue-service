package vandebron.http

import akka.actor.typed.scaladsl.AskPattern.{Askable, _}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import vandebron.service.QueueServiceActor.{ActionPerformed, Publish, Subscribe}
import vandebron.service.{HttpRequests, QueueServiceActor}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class QueueRoute(queue: ActorRef[QueueServiceActor.Command], subscribeDuration: FiniteDuration)(implicit val system: ActorSystem[_]) extends BaseRoute {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import vandebron.http.serialization.JsonFormats._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("queuing-service.routes.ask-timeout"))

  def publish(requests: HttpRequests): Future[ActionPerformed] =
    queue.ask(Publish(requests, _))

  def subscribe = queue.ask(Subscribe(_, subscribeDuration))

  def route: Route =
    pathPrefix("queue") {
      concat(
        (path("publish") & post)(publishRequests),
        (path("subscribe") & get)(subscribeResponses)
      )
    }

  private def publishRequests: Route =
    entity(as[HttpRequests]) { req =>
      complete {
        publish(req)
      }
    }

  private def subscribeResponses: Route =
      complete { subscribe }
}
