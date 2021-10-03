package vandebron.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import vandebron.domain.{CustomerOrderDetails, CustomerProductDetails}
import vandebron.service.QueueServiceActor.{ActionPerformed, Command, Publish, Subscribe}

import java.util.UUID
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

final case class HttpRequests(uris: Seq[String])
final case class CustomerDetailsResponse(orderDetails: CustomerOrderDetails, productDetails: CustomerProductDetails)

object QueueServiceActor {
  sealed trait Command

  final case class Publish(requests: HttpRequests, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class Subscribe(replyTo: ActorRef[Future[CustomerDetailsResponse]], durationLeft: FiniteDuration) extends Command
  final case class ActionPerformed(description: String)
}

class QueueServiceActor(
  orderApiService: OrderApiService,
  productApiService: ProductApiService,
  interval: FiniteDuration,
  cap: Int)(implicit ec: ExecutionContext) extends QueueServiceHelper {
  private val uniqueTimerKey = s"QueueActor-${UUID.randomUUID().toString}"

  def apply(): Behavior[Command] = queue(Seq.empty)

  private def queue(queryRequests: Seq[HttpRequests]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Publish(requests, replyTo) =>
        replyTo ! ActionPerformed(s"Following requests were published: ${requests.uris}")
        queue(queryRequests.appended(requests))
      case Subscribe(replyTo, durationLeft) =>
        val (orderRequests, productRequests) = splitRequests(queryRequests.flatMap(_.uris))
        if (checkQueueCap(orderRequests, productRequests, cap) || checkDuration(durationLeft)) {
          replyTo ! apiCalls(orderRequests, productRequests)
          queue(Seq.empty)
        } else {
          Behaviors.withTimers { timers =>
            timers.startSingleTimer(uniqueTimerKey, Subscribe(replyTo, durationLeft - interval), interval)
            Behaviors.same
          }
        }
    }

  def apiCalls(orderRequests: Seq[String], productRequests: Seq[String]): Future[CustomerDetailsResponse] = {
    val orderResponsesFuture = orderApiService.getOrderResponses(bulkRequests(orderRequests, orderApiPrefix))
    val productResponsesFuture = productApiService.getProductResponses(bulkRequests(productRequests, productApiPrefix))
    for {
      orderResponses <- orderResponsesFuture
      productResponses <- productResponsesFuture
    } yield { CustomerDetailsResponse(orderDetails = orderResponses, productDetails = productResponses) }
  }
}
