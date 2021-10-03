package vandebron.service

import vandebron.Config

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait QueueServiceHelper {
  val baseUrl = s"http://${Config.Http.host}:${Config.Http.port}"
  val orderApiPrefix = s"$baseUrl/order?q="
  val productApiPrefix = s"$baseUrl/product?q="

  protected def splitRequests(requests: Seq[String]): (Seq[String], Seq[String]) = {
    (requests.filter(uri => uri.startsWith(orderApiPrefix)), requests.filter(uri => uri.startsWith(productApiPrefix)))
  }

  protected def bulkRequests(requests: Seq[String], apiPattern: String): String = {
    s"""$apiPattern${requests.map(request => request.replace(apiPattern, "")).mkString(",")}"""
  }

  protected def checkQueueCap(orderRequests: Seq[String], productRequests: Seq[String], cap: Int): Boolean = {
    orderRequests.length > cap || productRequests.length > cap
  }

  protected def checkDuration(duration: FiniteDuration): Boolean = { duration <= 0.seconds }
}
