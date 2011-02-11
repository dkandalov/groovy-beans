package ru

import org.junit.Test

/**
 * User: dima
 * Date: 8/2/11
 */
class BeanTest {
  @Test public void shouldStoreValuesAssignedToNotExistingProperties() {
    def bean = new Bean()
    bean.a = 123
    bean.b = "abc"

    assert bean.a == 123
    assert bean.b == "abc"
  }

  @Test public void shouldBeAbleToSetPropertyWithTheSameNameAsInternalMap() {
    def bean = new Bean()
    bean.data = 123
    assert bean.data == 123
  }

  @Test public void equalityShouldWorkForDynamicProperties() {
    def bean1 = new Bean()
    def bean2 = new Bean()
    assert [:] == [:]
    assert bean1.equals(bean2)

    bean1.a = 123
    assert bean1 != bean2

    bean2.a = 123
    assert bean1 == bean2
    assert bean1.a == bean2.a
  }

  @Test public void shouldHaveToStringMethod() {
    def bean = new Bean()
    bean.a = 1.0

    assert bean.toString() == "[a:1.0]"
  }
}
