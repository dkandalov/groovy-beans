package com

import org.junit.Test
import com.cmcmarkets.Util

/**
 * User: DKandalov
 */
class UtilTest {
  @Test void asyncShouldDoTasksAsynchronously() {
    def i = Util.async { 123 }
    assert i.get() == 123
  }

  @Test void asyncShouldReturnDefaultValueIfItFails() {
    def i = Util.async(234) { throw new UnsupportedOperationException() }
    assert i.get() == 234
  }
}
