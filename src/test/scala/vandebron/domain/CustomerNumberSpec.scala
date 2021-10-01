package vandebron.domain

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CustomerNumberSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  private val numericChar = Gen.oneOf("0123456789".toSeq)
  val customerNumberString: Gen[String] = Gen
    .oneOf(
      Gen.listOfN(9, numericChar),
      Gen.listOfN(9, numericChar),
      Gen.listOfN(9, numericChar),
      Gen.listOfN(9, numericChar),
      Gen.listOfN(9, numericChar),
      Gen.listOfN(9, numericChar)
    )
    .map(_.mkString)

  "CustomerNumber" should {
    "be created" when {
      "from a 9 digit string" in {
        forAll(customerNumberString)(str => CustomerNumber.from(str) should be(Symbol("right")))
      }
    }
    "not be created" when {
      "from an string that contains a nonnumerical character" in {
        CustomerNumber.from("12345678A") should be(Symbol("left"))
      }
      "from a number string that's an invalid length" in {
        CustomerNumber.from("12345678910") should be(Symbol("left"))
        CustomerNumber.from("#12345678") should be(Symbol("left"))
        CustomerNumber.from("1234567") should be(Symbol("left"))
        CustomerNumber.from("1") should be(Symbol("left"))
      }
    }
  }
}
