package vandebron.http.dto

import vandebron.domain.{CustomerNumber, CustomerProductDetails}
import vandebron.domain.EnergyProduct.EnergyProduct

object CustomerProductsDto {
  def toDto(customerProducts: Set[(CustomerNumber, Seq[EnergyProduct])]): CustomerProductDetails = {
    customerProducts.flatMap(c => Map(c._1.value -> c._2.map(_.toString))).toMap
  }
}
