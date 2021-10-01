package vandebron.http.dto

import vandebron.domain.CustomerNumber
import vandebron.domain.EnergyProduct.EnergyProduct

object CustomerProductsDto {
  def toDto(customerProducts: Set[(CustomerNumber, Seq[EnergyProduct])]): Map[String, Seq[String]] = {
    customerProducts.flatMap(c => Map(c._1.value -> c._2.map(_.toString))).toMap
  }
}
