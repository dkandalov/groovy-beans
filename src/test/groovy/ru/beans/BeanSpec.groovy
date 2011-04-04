package ru.beans

import org.junit.Test
import static ru.beans.Bean.bean

 /**
 * User: dima
 * Date: 8/2/11
 */
class BeanSpec {
  @Test void shouldStoreValuesAssignedToNotExistingProperties() {
    def bean = new Bean()
    bean.a = 123
    bean.b = "abc"

    assert bean.a == 123
    assert bean.b == "abc"
  }

  @Test void shouldReturnNullForNotExistingProperties() {
    def bean = new Bean([a: 123])
    assert bean.a == 123
    assert bean.b == null
  }

  @Test void shouldBeAbleToSetPropertyWithTheSameNameAsInternalMap() {
    def bean = new Bean()
    bean.data = 123
    assert bean.data == 123
  }

  @Test void equalityShouldWorkForDynamicProperties() {
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

  @Test void shouldHaveToStringMethod() {
    def bean = new Bean()
    bean.a = 1.0

    assert bean.toString() == "bean[a:1.0]"
  }

  @Test void shouldUseTypeWhenPropertyIsAssigned() {
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

  @Test void shouldUseTypeWhenBeanIsCreatedFromMap() {
    def data = [name: "aName", id: "123", price: "1.23"]
    def bean = new Bean(data, [name: BeanType.STRING, id: BeanType.INTEGER, price: BeanType.DOUBLE])

    assert bean.name == "aName"
    assert bean.id == 123
    assert bean.price == 1.23
  }

  @Test void shouldReturnBeanFieldNames() {
    def bean = bean([instrumentId: "A", sIZe: 123])
    assert bean.fieldNames() == ["instrumentId", "sIZe"]
  }

  @Test void shouldReturnBeanValuesForSpecifiedFields() {
    def bean = bean([field1: 1, field2: 2, field3: 3])
    assert bean.fieldValuesFor(["field2", "field3"]) == [2, 3]
  }

  @Test void mergingShouldChangeLeftSideBean() {
    def bean1 = bean([a: 1, b: 2])
    def bean2 = bean([c: 3])

    assert bean1.mergeWith(bean2) == bean([a: 1, b: 2, c: 3])
    assert bean1.c == 3
    assert bean2.a == null
  }

  @Test void shouldMergeWithAnotherBean_UsingAccumulationClosure() {
    def bean1 = bean([a: 1, b: 2])
    def bean2 = bean([a: 1, b: 3, c: 4])

    def mergedBean = bean1.mergeWith(bean2) { b1, b2 ->
      b1.a += b2.a
      b1.b *= b2.b
    }
    assert mergedBean == bean([a: 2, b: 6, c: 4])
  }

  @Test void shouldChangeFieldName() {
    def bean = bean([a: 1, b: 2])
    bean.renameField("b", "c")
    assert bean.b == null
    assert bean.c == 2
  }
}
