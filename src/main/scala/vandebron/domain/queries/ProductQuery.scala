package vandebron.domain.queries

import vandebron.domain.CustomerNumber
import vandebron.domain.EnergyProduct.EnergyProduct
import vandebron.domain.repository.ProductRepository

class ProductQuery(productRepository: ProductRepository) {

  def findProducts(customerNumbers: Set[CustomerNumber]): Set[(CustomerNumber, Seq[EnergyProduct])] = {
    customerNumbers.map(customerNumber => (customerNumber -> productRepository.byCustomerNumber(customerNumber)))
  }
}
