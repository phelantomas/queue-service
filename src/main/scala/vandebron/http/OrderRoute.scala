package vandebron.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vandebron.domain.CustomerNumber
import vandebron.domain.queries.OrderQuery
import vandebron.http.dto.CustomerOrdersDto
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._

class OrderRoute(orderStatusQuery: OrderQuery) extends BaseRoute {

  def route: Route =
    (get & path("order") & parameter("q".as[Set[CustomerNumber]])) (getCustomerOrders)

  private def getCustomerOrders(customerNumbers: Set[CustomerNumber]): Route = complete {
    CustomerOrdersDto.toDto(orderStatusQuery.findOrderStatuses(customerNumbers))
  }
}
