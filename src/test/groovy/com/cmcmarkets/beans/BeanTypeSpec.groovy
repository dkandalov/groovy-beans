package com.cmcmarkets.beans

import org.junit.Test
import com.cmcmarkets.Util
import static com.cmcmarkets.Util.date

/**
 * User: dima
 * Date: 13/2/11
 */
class BeanTypeSpec {
  def shouldFail = new GroovyTestCase().&shouldFail

  @Test public void shouldConvertIntoDate() {
    def bean = new Bean().withType(date: BeanType.DATE("MM/yyyy"))
    bean.date = "02/2012"
    assert bean.date == date(1, 2, 2012)

    bean.date = date(1, 3, 2012).getTime()
    assert bean.date == date(1, 3, 2012)

    bean.date = date(1, 4, 2012).getTime().toString()
    assert bean.date == date(1, 4, 2012)

    shouldFail { bean.date = "some stuff which is not properly formatted" }
  }

  @Test public void shouldConvertDateIntoFormattedString() {
    def beanType = BeanType.DATE_AS_STRING("MM/yyyy")
    assert beanType.convert(Util.date(1, 2, 2011)) == "02/2011"
  }

  @Test public void shouldConvertIntoString() {
    def bean = new Bean().withType(s: BeanType.STRING)

    bean.s = 123
    assert bean.s == "123"
    bean.s = 1.2
    assert bean.s == "1.2"
  }

  @Test public void shouldConvertIntoInteger() {
    def bean = new Bean().withType(i: BeanType.INTEGER)

    bean.i = 123
    assert bean.i == 123

    bean.i = "234"
    assert bean.i == 234

    shouldFail {bean.i = "not a number"}
    shouldFail {bean.i = new BigDecimal(123)}
  }

  @Test public void shouldConvertIntoDouble() {
    def bean = new Bean().withType(d: BeanType.DOUBLE)

    bean.d = 123
    assert bean.d == 123.0

    bean.d = "234.5"
    assert bean.d == 234.5

    shouldFail {bean.d = "not a number"}
    shouldFail {bean.d = new BigDecimal(123)}
  }
}
