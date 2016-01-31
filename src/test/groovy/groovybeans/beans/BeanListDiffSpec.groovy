package groovybeans.beans

import org.junit.Test
import static groovybeans.beans.Bean.bean
import static groovybeans.beans.Bean.beans

 /**
 * User: dima
 * Date: 22/2/11
 */
class BeanListDiffSpec {
  def shouldFail = new GroovyTestCase().&shouldFail

  @Test public void exactlySameBeanListsShouldHaveEmptyDiff_EvenWhenOrderedDifferently() {
    def diff = BeanListDiff.diff(
            beans([a: "A", b: 1], [a: "AA", b: 2]),
            beans([a: "AA", b: 2], [a: "A", b: 1]),
            ["a"], ["b"]
    )
    assert diff.left == []
    assert diff.diff == []
    assert diff.right == []
  }

  @Test public void differentBeanListsShouldHaveNotEmptyDiff() {
    def diff = BeanListDiff.diff(
            beans([a: "A", b: 1], [a: "AA", b: 2], [a: "AAA", b: 3]),
            beans([a: "AA", b: 222], [a: "AAA", b: 3], [a: "AAAA", b: 4], [a: "AAAAA", b: 5]),
            ["a"], ["b"]
    )
    assert diff.left == [bean([a: "A", b: 1])]
    assert diff.right == [bean([a: "AAAAA", b: 5]), bean([a: "AAAA", b: 4])]
    assert diff.diff == [[[], ["b"], [], bean([a: "AA", b: 2]), bean([a: "AA", b: 222])] as BeanDiff]
  }

  @Test public void shouldThrowException_IfThereIsDifferentAmountOfBeansWithTheSameKey() {
    shouldFail {
      BeanListDiff.diff(
              beans([a: "A", b: 1], [a: "A", b: 2]), // two beans with the same key
              beans([a: "A", b: 2]),
              ["a"], ["b"]
      )
    }
  }

  @Test public void shouldCompareBeanListsUsingComparator() {
    def customComparator = {
      def someWeirdCondition = (bean2.key == "B" && beans1.find {it.value == 2})
      if (someWeirdCondition) return BeanDiff.NO_DIFF

      //noinspection GroovyAssignabilityCheck
      BeanDiff.diff(bean1, bean2, fieldsToCompare)
    }

    assert BeanListDiff.diffWithComparator(
            beans([key: "A", value: 1], [key: "B", value: 2]),
            beans([key: "A", value: 1], [key: "B", value: 22]),
            ["key"], ["value"], customComparator
    ).match()
    assert !BeanListDiff.diff(
            beans([key: "A", value: 1], [key: "B", value: 2]),
            beans([key: "A", value: 1], [key: "B", value: 22]),
            ["key"], ["value"]
    ).match()
  }
}
