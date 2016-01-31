package groovybeans.livecompare


class ListComparator {
  private List list1
  private List list2
  private Map result

  Map compare(List list1, List list2) {
    this.list1 = list1
    this.list2 = list2

    result = [:]
    for (def match = findNextMatch(); match != null; match = findNextMatch()) {
      consumeListsBeforeMatch(match)
      consumeMatch()
    }
    result
  }

  def findNextMatch() {
    if (list1.empty || list2.empty) return null

    def positionIterator = new PositionIterator(list1.size(), list2.size())
    for (def position: positionIterator) {
      if (list1[position[0]] == list2[position[1]]) return position
    }

    null
  }

  def consumeListsBeforeMatch(lastMatch) {
    def leftDiff = new ArrayList(list1.subList(0, lastMatch[0]))
    def rightDiff = new ArrayList(list2.subList(0, lastMatch[1]))
    lastMatch[0].times {list1.remove(0)}
    lastMatch[1].times {list2.remove(0)}

    if (!leftDiff.empty) {
      if (!result.containsKey("left")) result.put("left", [])
      result.get("left").addAll(leftDiff)
    }
    if (!rightDiff.empty) {
      if (!result.containsKey("right")) result.put("right", [])
      result.get("right").addAll(rightDiff)
    }
  }

  def consumeMatch() {
    while (!list1.empty && !list2.empty && list1[0] == list2[0]) {
      list1.remove(0)
      list2.remove(0)
    }
  }

  /**
   * This class represents the following code with lazy evaluation:
   *
   * <pre>
   * def result = []
   * for (int i = 0; i < Math.min(size1, size2); i++) {
   *   for (int j = i; j < Math.max(size1, size2); j++) {
   *      if (j < size2) result << [i, j]
   *     if (i != j && j < size1) result << [j, i]
   *   }
   * }
   * result
   * </pre>
   *
   * This will probably have O(n^2) complexity but assuming that lists for comparison are not big it shouldn't matter.
   */
  static class PositionIterator implements Iterable, Iterator {
    int size1
    int size2
    int minSize
    int maxSize

    int i, j
    def results = []

    PositionIterator(int size1, int size2) {
      this.size1 = size1
      this.size2 = size2
      this.minSize = Math.min(size1, size2)
      this.maxSize = Math.max(size1, size2)
    }

    @Override Object next() {
      if (i < minSize) {
        if (j < size2) results << [i, j]
        if (i != j && j < size1) results << [j, i]

        j++
        if (j == maxSize) {
          i++
          j = i
        }
      }

      results.remove(0)
    }

    @Override boolean hasNext() {
      i < minSize || !results.empty
    }

    @Override Iterator iterator() {
      return this
    }

    @Override void remove() {
      throw new UnsupportedOperationException()
    }
  }
}
