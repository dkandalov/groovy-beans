package ru.beans

import org.junit.Test
import static ru.beans.Bean.bean

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanDiffTest {
  @Test public void emptyBeansShouldAlwaysMatch() {
    def bean1 = bean()
    def bean2 = bean()
    assert BeanDiff.diff(bean1, bean2).match()
    assert BeanDiff.diff(bean2, bean1).match()
  }

  @Test public void shouldFindDifferencesBetweenBeans() {
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

  @Test public void shouldFindFieldsMissingInOtherBean() {
    def bean1 = bean([a: "A", b: 2])
    def bean2 = bean([a: "A"])

    assert BeanDiff.diff(bean1, bean1).left == []
    assert BeanDiff.diff(bean1, bean1).right == []

    assert BeanDiff.diff(bean1, bean2).left == ["b"]
    assert BeanDiff.diff(bean1, bean2).right == []
    assert BeanDiff.diff(bean2, bean1).left == []
    assert BeanDiff.diff(bean2, bean1).right == ["b"]
  }
}
