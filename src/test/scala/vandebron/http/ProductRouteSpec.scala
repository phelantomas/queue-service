package vandebron.http

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import vandebron.domain.queries.ProductQuery
import vandebron.domain.{CustomerNumber, EnergyProduct}

class ProductRouteSpec extends AnyWordSpec with ScalatestRouteTest with Matchers with MockitoSugar {

  trait TestScope {
    val query = mock[ProductQuery]
    val productRoute = new ProductRoute(query)
    val route = Route.seal(productRoute.route)
    val customerNumber: CustomerNumber = CustomerNumber.unsafeFrom("123456782")

    when(query.findProducts(Set(customerNumber))) thenReturn Set((customerNumber -> Seq(EnergyProduct.ELECTRICITY, EnergyProduct.EV)))
  }

  "OrderRoute GET" should {
    "return customer products" in new TestScope {
      Get(s"/product?q=$customerNumber") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query).findProducts(Set(customerNumber))
        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===(s"""{"$customerNumber":["ELECTRICITY","EV"]}""")
      }
    }

    "only use a customerNumber once" in new TestScope {
      Get(s"/product?q=$customerNumber,$customerNumber,$customerNumber") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query, times(1)).findProducts(Set(customerNumber))

        entityAs[String] should ===(s"""{"$customerNumber":["ELECTRICITY","EV"]}""")
      }
    }

    "not attempt to use invalid customer numbers" in new TestScope {
      Get(s"/product?q=$customerNumber,12345678A,12345678,qqq,,") ~> route ~> check {
        status shouldBe StatusCodes.OK
        verify(query, times(1)).findProducts(Set(customerNumber))
        entityAs[String] should ===(s"""{"$customerNumber":["ELECTRICITY","EV"]}""")
      }
    }
  }
}
