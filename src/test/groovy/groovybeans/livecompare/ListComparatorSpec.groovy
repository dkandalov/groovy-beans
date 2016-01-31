package groovybeans.livecompare

import org.junit.Test


class ListComparatorSpec {
  @Test public void shouldNotDoAnything_WhenListsAreEmpty() {
    assert new ListComparator().compare([], []) == [:]
  }

  @Test public void shouldWorkWhenListsAreCompletelyDifferent() {
    assert new ListComparator().compare([1, 2, 3], [4, 5, 6]) == [:]
  }

  @Test public void shouldFindNotMatchingValuesInTheLeftList() {
    // prefix difference
    given([-1, 0, 1, 2, 3], [1, 2, 3]) { leftList, rightList ->
      assert new ListComparator().compare(leftList, rightList) == [left: [-1, 0]]
      assert leftList == []
      assert rightList == []
    }
    // infix difference
    given([1, 8, 8, 2, 3], [1, 2, 3]) { leftList, rightList ->
      assert new ListComparator().compare(leftList, rightList) == [left: [8, 8]]
      assert leftList == []
      assert rightList == []
    }
    // postfix difference
    given([1, 2, 3, 5, 6], [1, 2, 3]) { leftList, rightList ->
      assert new ListComparator().compare(leftList, rightList) == [:] // ignore postfix difference
      assert leftList == [5, 6]
      assert rightList == []
    }
    // should choose minimal difference
    given([0, 1, 2, 3], [1, 2, 3, 0]) { leftList, rightList ->
      assert new ListComparator().compare(leftList, rightList) == [left: [0]]
      assert leftList == []
      assert rightList == [0]
    }

    // should find several differences
    given([1, -1, -1, 2, 3, -3, 4], [1, 2, -2, -2, 3, 4]) { leftList, rightList ->
      assert new ListComparator().compare(leftList, rightList) == [left: [-1, -1, -3], right: [-2, -2]]
      assert leftList == []
      assert rightList == []
    }
  }

  private def given(List leftList, List rightList, Closure closure) {
    closure(leftList, rightList)
  }

  @Test public void shouldIterateThroughPositions() {
    def resultFor = { size1, size2 ->
      new ListComparator.PositionIterator(size1, size2).collect {it}
    }

    assert resultFor(1, 1) == [[0, 0]]

    assert resultFor(2, 2) == [[0, 0], [0, 1], [1, 0], [1, 1]]
    assert resultFor(1, 2) == [[0, 0], [0, 1]]
    assert resultFor(2, 1) == [[0, 0], [1, 0]]

    assert resultFor(3, 3) == [[0, 0], [0, 1], [1, 0], [0, 2], [2, 0], [1, 1], [1, 2], [2, 1], [2, 2]]
    assert resultFor(2, 3) == [[0, 0], [0, 1], [1, 0], [0, 2], [1, 1], [1, 2]]
    assert resultFor(3, 2) == [[0, 0], [0, 1], [1, 0], [2, 0], [1, 1], [2, 1]]
  }

}
