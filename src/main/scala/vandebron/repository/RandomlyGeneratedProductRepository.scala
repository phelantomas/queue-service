package vandebron.repository

import vandebron.domain.EnergyProduct.EnergyProduct
import vandebron.domain.repository.ProductRepository
import vandebron.domain.{CustomerNumber, EnergyProduct}

class RandomlyGeneratedProductRepository extends ProductRepository {

  def byCustomerNumber(customerNumber: CustomerNumber): Seq[EnergyProduct] = retrieveProducts(customerNumber)

  private def retrieveProducts(customerNumber: CustomerNumber): Seq[EnergyProduct] = {
    val number = customerNumber.value.last.asDigit
    val size = EnergyProduct.values.size
    val shuffledProducts = scala.util.Random.shuffle(EnergyProduct.values.toSeq)

    if (number <= size) { shuffledProducts.take(number) }
    else { shuffledProducts ++ padOutProducts(number, size) }
  }

  private def padOutProducts(number: Int, size: Int) = {
    (1 to (number - size)).map(_ => EnergyProduct(scala.util.Random.nextInt(EnergyProduct.maxId)))
  }
}
