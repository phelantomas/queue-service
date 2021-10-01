package vandebron.domain

object OrderStatus extends Enumeration {
  type OrderStatus = Value
  val NEW, ORDERING, SETUP, DELIVERED = Value
}
