package vandebron.repository

import vandebron.domain.OrderStatus.OrderStatus
import vandebron.domain.repository.OrderRepository
import vandebron.domain.{CustomerNumber, OrderStatus}

class RandomlyGeneratedOrderRepository extends OrderRepository {
   def byCustomerNumber(customerNumber: CustomerNumber): OrderStatus = OrderStatus(scala.util.Random.nextInt(OrderStatus.maxId))
}
