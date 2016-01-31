package groovybeans

import org.junit.Test

import static groovybeans.Util.date
import static groovybeans.Util.parseDate


class UtilTest {
  @Test public void shouldParseDateFromDefaultDateToString() {
    def aDate = date(23, 9, 2011)
    def dateAsString = aDate.toString()
    assert parseDate(dateAsString) == aDate
  }

  @Test void asyncShouldDoTasksAsynchronously() {
    def i = Util.async { 123 }
    assert i.get() == 123
  }

  @Test void asyncShouldReturnDefaultValueIfItFails() {
    def i = Util.async(234) { throw new UnsupportedOperationException() }
    assert i.get() == 234
  }
}
