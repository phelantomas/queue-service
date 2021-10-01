package vandebron.domain.repository

import vandebron.domain.CustomerNumber
import vandebron.domain.OrderStatus.OrderStatus

trait OrderRepository {
  def byCustomerNumber(customerNumber: CustomerNumber): OrderStatus
}
