package vandebron.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vandebron.domain.CustomerNumber
import vandebron.domain.queries.ProductQuery
import vandebron.http.dto.CustomerProductsDto
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._

class ProductRoute(productQuery: ProductQuery) extends BaseRoute {

  def route: Route =
    (get & path("product") & parameter("q".as[Set[CustomerNumber]])) (getCustomerOrders)

  private def getCustomerOrders(customerNumbers: Set[CustomerNumber]): Route = complete {
    CustomerProductsDto.toDto(productQuery.findProducts(customerNumbers))
  }
}
