package ru

import org.junit.Test
import static ru.Util.date

/**
 * User: dima
 * Date: 13/2/11
 */
class BeanTypeTest {
  @Test public void aaa() {
    def type = BeanType.DATE("MM/yyyy")
    assert type.convert("02/2012") == date(01, 02, 2012)
  }
}
