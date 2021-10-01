package vandebron.domain.repository

import eu.timepit.refined.auto._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import vandebron.domain.EnergyProduct
import vandebron.repository.RandomlyGeneratedProductRepository

class RandomlyGeneratedProductRepositorySpec extends AnyWordSpec with Matchers {

  val repo = new RandomlyGeneratedProductRepository

  "RandomlyGeneratedOrderStatusRepository" when {
    "retrieving Products" should {
      "return a list of products of the correct length" in {
        repo.byCustomerNumber("123456789").length shouldBe 9
        repo.byCustomerNumber("123456784").length shouldBe 4
        repo.byCustomerNumber("123456783").length shouldBe 3
        repo.byCustomerNumber("123456782").length shouldBe 2
        repo.byCustomerNumber("123456781").length shouldBe 1
        repo.byCustomerNumber("123456780").length shouldBe 0
      }

      "return a list of products that contain at least all products" in {
        repo.byCustomerNumber("123456783").toSet shouldBe EnergyProduct.values
        repo.byCustomerNumber("123456789").toSet shouldBe EnergyProduct.values
      }
      "return a list of products that has no repeating products" in {
        repo.byCustomerNumber("123456782").toSet.size shouldBe 2
        repo.byCustomerNumber("123456783").toSet.size shouldBe 3
      }
    }
  }
}
