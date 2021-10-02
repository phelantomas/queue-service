package vandebron.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import vandebron.Config
import vandebron.service.QueueServiceActor.{ActionPerformed, Command, Enqueue, GetCustomerDetails}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

final case class HttpRequests(uris: Seq[String])

final case class CustomerDetailsResponse(orderDetails: Map[String, String], productDetails: Map[String, Seq[String]])

object QueueServiceActor {
  sealed trait Command

  final case class Enqueue(requests: HttpRequests, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetCustomerDetails(replyTo: ActorRef[Future[Option[CustomerDetailsResponse]]]) extends Command

  final case class ActionPerformed(description: String)
}

class QueueServiceActor(orderApiService: OrderApiService, productApiService: ProductApiService)(implicit ec: ExecutionContext) {
  val baseUrl = s"http://${Config.Http.host}:${Config.Http.port}"
  val orderApiPrefix = s"$baseUrl/order?q="
  val productApiPrefix = s"$baseUrl/product?q="

  def apply(): Behavior[Command] = queue(Seq.empty)

  private def queue(queryRequests: Seq[HttpRequests]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Enqueue(requests, replyTo) =>
        replyTo ! ActionPerformed(s"Following requests were added to queue: ${requests.uris}")
        queue(queryRequests.appended(requests))
      case GetCustomerDetails(replyTo) =>
        val (orderRequests, productRequests) = splitRequests(queryRequests.flatMap(_.uris))
        if (checkQueueCap(orderRequests, productRequests)) {
          val orderResponsesFuture = orderApiService.getOrderResponses(bulkRequests(orderRequests, orderApiPrefix))
          val productResponsesFuture = productApiService.getProductResponses(bulkRequests(productRequests, productApiPrefix))
          val futureResponse = for {
            orderResponses <- orderResponsesFuture
            productResponses <- productResponsesFuture
          } yield {
            Some(CustomerDetailsResponse(orderDetails = orderResponses, productDetails = productResponses))
          }
          replyTo ! futureResponse
          queue(Seq.empty)
        } else {
          replyTo ! Future {
            None
          }
          Behaviors.same
        }
    }

  private def splitRequests(requests: Seq[String]): (Seq[String], Seq[String]) = {
    (requests.filter(uri => uri.startsWith(orderApiPrefix)), requests.filter(uri => uri.startsWith(productApiPrefix)))
  }

  private def checkQueueCap(orderRequests: Seq[String], productRequests: Seq[String]): Boolean = {
    val queueCap = Config.Queue.cap
    orderRequests.length > queueCap || productRequests.length > queueCap
  }

  private def bulkRequests(requests: Seq[String], apiPattern: String): String = {
    s"""$apiPattern${requests.map(request => request.replace(apiPattern, "")).mkString(",")}"""
  }
}
