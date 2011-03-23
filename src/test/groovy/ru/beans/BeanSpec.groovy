package ru.beans

import org.junit.Test
import static ru.beans.Bean.bean

/**
 * User: dima
 * Date: 8/2/11
 */
class BeanSpec {
  @Test public void shouldStoreValuesAssignedToNotExistingProperties() {
    def bean = new Bean()
    bean.a = 123
    bean.b = "abc"

    assert bean.a == 123
    assert bean.b == "abc"
  }

  @Test public void shouldReturnNullForNotExistingProperties() {
    def bean = new Bean([a: 123])
    assert bean.a == 123
    assert bean.b == null
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

    assert bean.toString() == "bean[a:1.0]"
  }

  @Test public void shouldUseTypeWhenPropertyIsAssigned() {
    def bean = new Bean().withType([name: BeanType.STRING, id: BeanType.INTEGER, price: BeanType.DOUBLE])

    bean.name = "aName"
    assert bean.name == "aName"
    bean.name = 123
    assert bean.name == "123"

    bean.id = 123
    assert bean.id == 123
    bean.id = "234"
    assert bean.id == 234

    bean.price = 1.2d
    assert bean.price == 1.2
    bean.price = "1.23"
    assert bean.price == 1.23
  }

  @Test public void shouldUseTypeWhenBeanIsCreatedFromMap() {
    def data = [name: "aName", id: "123", price: "1.23"]
    def bean = new Bean(data, [name: BeanType.STRING, id: BeanType.INTEGER, price: BeanType.DOUBLE])

    assert bean.name == "aName"
    assert bean.id == 123
    assert bean.price == 1.23
  }

  @Test public void shouldReturnBeanFields() {
    def bean = bean([instrumentId: "A", sIZe: 123])
    assert bean.fieldNames() == ["instrumentId", "sIZe"]
  }

  @Test public void shouldReturnBeanValuesForSpecifiedFields() {
    def bean = bean([field1: 1, field2: 2, field3: 3])
    assert bean.fieldValues(["field2", "field3"]) == [2, 3]
  }

  @Test public void shouldIterateOverFieldValuesAndNames() {
    def bean = bean([field1: 1, field2: 2, field3: 3])
    def actual = [:]
    bean.eachValue { value, name ->
      actual.put(value, name)
    }
    assert actual == [field1: 1, field2: 2, field3: 3]
  }

  @Test public void shouldMergeWithBean_WithoutChangingExistingState() {
    def bean1 = bean([a: 1, b: 2])
    def bean2 = bean([c: 3])
    def bean3 = bean([a: 1, b: 2, c: 3])

    assert bean1.mergeWith(bean2) == bean3
    assert bean1.c == null
    assert bean2.a == null
  }

  @Test public void shouldMergeWithBean_UsingAccumulationClosure() {
    fail // TODO stopped here
  }

  @Test public void shouldBeCaseInsensitive() {
    def bean = bean([instrumentId: "A"])
    bean.instrumentId = 123
    assert bean.InstrumentID == 123

    bean.instrumentid = 234
    assert bean.instrumentid == 234
  }
}
