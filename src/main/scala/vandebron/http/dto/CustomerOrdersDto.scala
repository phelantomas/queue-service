package vandebron.http.dto

import vandebron.domain.{CustomerNumber, CustomerOrderDetails}
import vandebron.domain.OrderStatus.OrderStatus

object CustomerOrdersDto {
  def toDto(customerOrders: Set[(CustomerNumber, OrderStatus)]): CustomerOrderDetails = {
    customerOrders.flatMap(c => Map(c._1.value -> c._2.toString)).toMap
  }
}
