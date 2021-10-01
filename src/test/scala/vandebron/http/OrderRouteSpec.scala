package vandebron.http

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import vandebron.domain.{CustomerNumber, OrderStatus}
import vandebron.domain.queries.OrderQuery

class OrderRouteSpec extends AnyWordSpec with ScalatestRouteTest with Matchers with MockitoSugar {

  trait TestScope {
    val query = mock[OrderQuery]
    val orderRoute = new OrderRoute(query)
    val route = Route.seal(orderRoute.route)

    val customerNumber: CustomerNumber = CustomerNumber.unsafeFrom("123456789")

    when(query.findOrderStatuses(Set(customerNumber))) thenReturn Set((customerNumber -> OrderStatus.NEW))
  }

  "OrderRoute GET" should {
    "return customer order statuses" in new TestScope {
      Get(s"/order?q=$customerNumber") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query).findOrderStatuses(Set(customerNumber))
        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===(s"""{"$customerNumber":"NEW"}""")
      }
    }

    "only use a customerNumber once" in new TestScope {
      Get(s"/order?q=$customerNumber,$customerNumber,$customerNumber") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query, times(1)).findOrderStatuses(Set(customerNumber))

        entityAs[String] should ===(s"""{"$customerNumber":"NEW"}""")
      }
    }

    "not attempt to use invalid customer numbers" in new TestScope {
      Get(s"/order?q=$customerNumber,12345678A,12345678,qqq,,") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query, times(1)).findOrderStatuses(Set(customerNumber))
        entityAs[String] should ===(s"""{"$customerNumber":"NEW"}""")
      }
    }
  }
}
