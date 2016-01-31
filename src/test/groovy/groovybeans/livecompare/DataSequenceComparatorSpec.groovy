package groovybeans.livecompare

import java.util.concurrent.LinkedBlockingDeque
import org.junit.Test
import static java.util.concurrent.TimeUnit.*

/**
 * User: dima
 * Date: 20/3/11
 */
class DataSequenceComparatorSpec {
  private static final int PERIOD = 10

  @Test public void shouldNotProduceAnythingForEmptyInput() {
    def result = new LinkedBlockingDeque()
    new DataSequenceComparator().startComparingEvery(PERIOD, MILLISECONDS) { result.addLast(it) }
    Thread.sleep(PERIOD * 2)

    assert result.empty
  }

  @Test public void shouldFindNoDifferencesIfInputsAreTheSame() {
    def result = new LinkedBlockingDeque()
    def comparator = new DataSequenceComparator().startComparingEvery(PERIOD, MILLISECONDS) { result.addLast(it) }
    [1, 2, 3].each {
      comparator.add1(it)
      comparator.add2(it)
    }
    Thread.sleep(PERIOD * 2)

    assert result.empty
  }

  @Test public void shouldFindNoDifferencesIfInputsAreCompletelyDifferent() {
    def result = new LinkedBlockingDeque()
    def comparator = new DataSequenceComparator().startComparingEvery(PERIOD, MILLISECONDS) { result.addLast(it) }
    [1, 2, 3].each { comparator.add1(it) }
    [4, 5, 6].each { comparator.add2(it) }
    Thread.sleep(PERIOD * 2)

    assert result.empty
  }

  @Test public void shouldFindDifferencesForDifferentInputs() {
    def result = new LinkedBlockingDeque()
    def comparator = new DataSequenceComparator().startComparingEvery(PERIOD, MILLISECONDS) { result.addLast(it) }
    [1, 0, 0, 2, 3].each { comparator.add1(it) }
    [1, 2, 3].each { comparator.add2(it) }
    Thread.sleep(PERIOD * 2)
    assert result.takeFirst() == [left: [0, 0]]
    assert result.empty
  }

  @Test public void shouldNoticeChangedOverTime() {
    def result = new LinkedBlockingDeque()
    def comparator = new DataSequenceComparator().startComparingEvery(PERIOD, MILLISECONDS) { result.addLast(it) }
    [1, 2, 3, 4, 5].each { comparator.add1(it) }
    [1, 2, 3].each { comparator.add2(it) }
    Thread.sleep(PERIOD * 2)
    assert result.empty

    [-1, 4, 5].each { comparator.add2(it) }
    assert result.takeFirst() == [right: [-1]]
    assert result.empty
  }
}
