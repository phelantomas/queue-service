package vandebron.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import vandebron.Config
import vandebron.service.QueueServiceActor.{ActionPerformed, Command, Enqueue, QueryApis}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

final case class HttpRequests(uris: Seq[String])

final case class Requests(users: immutable.Seq[HttpRequests])

final case class CustomerDetailsResponse(orderDetails: Map[String, String], productDetails: Map[String, Seq[String]])

final case class CustomerDetailsResponses(details: Seq[CustomerDetailsResponse])

object QueueServiceActor {
  sealed trait Command

  final case class Enqueue(requests: HttpRequests, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class QueryApis(replyTo: ActorRef[Future[CustomerDetailsResponse]]) extends Command

  final case class ActionPerformed(description: String)
}

class QueueServiceActor(orderApiService: OrderApiService, productApiService: ProductApiService)(implicit ec: ExecutionContext) {
  def apply(): Behavior[Command] = queue(Set.empty)

  private def queue(queryRequests: Set[HttpRequests]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Enqueue(requests, replyTo) =>
        replyTo ! ActionPerformed(s"Following requests were added to queue: ${requests.uris}")
        queue(queryRequests + requests)
      case QueryApis(replyTo) =>
        val (orderRequests, productRequest) = splitRequests(queryRequests.flatMap(_.uris).toSeq)

        val orderResponses = Future.sequence(orderRequests.map(orderApiService.getOrderResponses))
        val productResponses = Future.sequence(productRequest.map(productApiService.getProductResponses))

        val futureResponse = for {
          orderResponses <- orderResponses
          productResponses <- productResponses
        } yield {
          CustomerDetailsResponse(orderDetails = orderResponses.flatten.toMap, productDetails = productResponses.flatten.toMap)
        }
        replyTo ! futureResponse
        queue(Set.empty)
    }

  private def splitRequests(requests: Seq[String]): (Seq[String], Seq[String]) = {
    val baseUrl = s"http://${Config.Http.host}:${Config.Http.port}"
    val orderApiPrefix = s"$baseUrl/order?q="
    val productApiPrefix = s"$baseUrl/product?q="

    (requests.filter(uri => uri.startsWith(orderApiPrefix)), requests.filter(uri => uri.startsWith(productApiPrefix)))
  }
}
