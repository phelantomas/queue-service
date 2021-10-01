package vandebron.domain.queries

import vandebron.domain.OrderStatus.OrderStatus
import vandebron.domain.CustomerNumber
import vandebron.domain.repository.OrderRepository

class OrderQuery(orderRepository: OrderRepository) {

  def findOrderStatuses(customerNumbers: Set[CustomerNumber]): Set[(CustomerNumber, OrderStatus)] = {
    customerNumbers.map(customerNumber => (customerNumber -> orderRepository.byCustomerNumber(customerNumber)))
  }
}
