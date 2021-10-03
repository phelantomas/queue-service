package vandebron.service

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QueueServiceHelperSpec extends AnyWordSpec with Matchers with QueueServiceHelper {

  val orderRequests = Seq(
    "http://localhost:4242/order?q=123456789,123456789",
    "http://localhost:4242/order?q=1,111111111,222222222,33333333",
    "http://localhost:4242/order?q=1,987654321,481516230"
  )

  val productRequests = Seq(
    "http://localhost:4242/product?q=123456789,123456789",
    "http://localhost:4242/product?q=33333333,111111111"
  )

  val invalidRequests = Seq(
    "http://localhost:4242/invalid?q=123456789,123456789",
    "http://localhost:4242/invalid?q=123456789,444444444"
  )

  val allRequest = orderRequests ++ productRequests ++ invalidRequests


  "QueueServiceHelper" when {
    "splitRequests" should {
      "return all order and product requests seperated" in {
        splitRequests(allRequest) shouldBe(orderRequests, productRequests)
      }
    }

    "bulkRequests" should {
      "bulk all requests into one based on a pattern" in {
        bulkRequests(orderRequests, orderApiPrefix) shouldBe "http://localhost:4242/order?q=123456789,123456789,1,111111111,222222222,33333333,1,987654321,481516230"
        bulkRequests(productRequests, productApiPrefix) shouldBe "http://localhost:4242/product?q=123456789,123456789,33333333,111111111"
      }
    }
  }
}
