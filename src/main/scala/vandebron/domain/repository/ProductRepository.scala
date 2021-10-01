package vandebron.domain.repository

import vandebron.domain.CustomerNumber
import vandebron.domain.EnergyProduct.EnergyProduct

trait ProductRepository {
  def byCustomerNumber(customerNumber: CustomerNumber): Seq[EnergyProduct]
}
