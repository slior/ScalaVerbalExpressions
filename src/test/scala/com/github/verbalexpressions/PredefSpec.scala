package com.github.verbalexpressions

import org.specs2.mutable.Specification

final class PredefSpec extends Specification
{

  import Predef._

  "positiveInteger" should {
    "identify simple integers" in {
      positiveInteger.test("123") should beTrue
    }

    "return false on empty input" in {
      positiveInteger.test("") should beFalse
    }

    "return false for decimal" in {
      positiveInteger.test("0.5") should beFalse
    }

    "return false for negative number" in { positiveInteger.test("-345") should beFalse }

    "return true for a number starting with 0" in { positiveInteger.test("03311") should beTrue }
  }



  "negative integer" should {
    "identify correctly a negative integer" in { negativeInteger.test("-54") should beTrue }

    "return false on empty input" in { negativeInteger.test("") should beFalse }

    "return false for decimal" in { negativeInteger.test("-0.5") should beFalse }

    "return false for positive number" in { negativeInteger.test("345") should beFalse }

    "return true for a number starting with 0" in { negativeInteger.test("-03311") should beTrue }

    "not allow whitespace between the minus sign and the number" in { negativeInteger.test("- 345") should beFalse }

  }

}
