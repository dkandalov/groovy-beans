package groovybeans.beans

import org.junit.Test
import static groovybeans.beans.Bean.bean


class BeanDiffSpec {
  @Test void emptyBeansShouldAlwaysMatch() {
    def bean1 = bean()
    def bean2 = bean()
    assert BeanDiff.diff(bean1, bean2).match()
    assert BeanDiff.diff(bean2, bean1).match()
  }

  @Test void shouldFindDifferencesBetweenBeans() {
    def bean1 = bean([a: "A", b: 2])
    def bean2 = bean([a: "A", b: 3])
    def bean3 = bean([a: "B", b: 3])

    assert BeanDiff.diff(bean1, bean1).diff == []

    assert BeanDiff.diff(bean1, bean2).diff == ["b"]
    assert BeanDiff.diff(bean2, bean1).diff == ["b"]

    assert BeanDiff.diff(bean2, bean3).diff == ["a"]
    assert BeanDiff.diff(bean3, bean2).diff == ["a"]

    assert BeanDiff.diff(bean1, bean3).diff == ["a", "b"]
    assert BeanDiff.diff(bean3, bean1).diff == ["a", "b"]
  }

  @Test void shouldFindFieldsMissingInOtherBean() {
    def bean1 = bean([a: "A", b: 2])
    def bean2 = bean([a: "A"])

    assert BeanDiff.diff(bean1, bean1).diff == []
    assert BeanDiff.diff(bean1, bean1).left == []
    assert BeanDiff.diff(bean1, bean1).right == []

    assert BeanDiff.diff(bean1, bean2).diff == []
    assert BeanDiff.diff(bean1, bean2).left == ["b"]
    assert BeanDiff.diff(bean1, bean2).right == []

    assert BeanDiff.diff(bean2, bean1).diff == []
    assert BeanDiff.diff(bean2, bean1).left == []
    assert BeanDiff.diff(bean2, bean1).right == ["b"]
  }

  @Test void shouldCompareBeansUsingOnlySpecifiedFields() {
    def bean1 = bean([a: "A", b: 2, c: 3])
    def bean2 = bean([a: "A", b: 2, c: 4])

    assert BeanDiff.diff(bean1, bean2, ["a", "b"]).match()
  }

  @Test void shouldCompareBeansUsingComparator() {
    def doubleComparator = {
      if (value1 instanceof Double && value2 instanceof Double)
        Math.abs((double) value1 - value2) < 0.01
      else
        value1 == value2
    }

    def bean1 = bean([a: "A", b: 2.0d])
    def bean2 = bean([a: "A", b: 2.01d])
    def bean3 = bean([a: "A", b: 2.011d])

    assert BeanDiff.diffWithComparator(bean1, bean2, doubleComparator).match()
    assert BeanDiff.diffWithComparator(bean2, bean3, doubleComparator).match()
    assert !BeanDiff.diffWithComparator(bean1, bean3, doubleComparator).match()
  }
}
