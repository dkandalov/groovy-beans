package groovybeans.beans

import org.junit.Test
import static groovybeans.beans.Bean.bean
import static groovybeans.beans.Bean.beans

class BeanTableSpec {
  @Test void shouldInsertValues() {
    def table = new BeanTable(["key"])
    table.insert(bean([key: 1, value: "a"]))
    table.insertAll(beans([key: 2, value: "b"], [key: 3, value: "c"]))

    assert table.selectAll() == beans([key: 1, value: "a"], [key: 2, value: "b"], [key: 3, value: "c"])
  }

  @Test void shouldReplaceExistingBeansWithNewValues_IfKeysAreTheSame() {
    def table = new BeanTable(["key"])
    table.insert(bean([key: 1, value: "a"]))
    table.insert(bean([key: 2, value: "b"]))
    table.insert(bean([key: 1, value: "A"]))

    assert table.selectAll() == beans([key: 1, value: "A"], [key: 2, value: "b"])
  }

  @Test void shouldAccumulateBeans_IfTableHasAccumulationClosure() {
    def table = new BeanTable(["key"])
    table.whenBeanExists { oldBean, newBean -> oldBean.value += newBean.value }

    table.insert(bean([key: "a", value: 1]))
    table.insert(bean([key: "a", value: 2, value2: "-"]))
    table.insert(bean([key: "b", value: 2, value2: "-"]))

    assert table.selectAll() == beans(
        [key: "a", value: 3, value2: "-"],
        [key: "b", value: 2, value2: "-"]
    )
  }

  @Test void shouldDoInnerJoin_UsingKeys() {
    def beans1 = (1..4).collect {bean(key1: it, key2: it + 1, value: "a" * it)}
    def beans2 = (2..3).collect {bean(key1: it, key2: it + 1, value2: "b" * it)}
    def table = new BeanTable(["key1", "key2"], beans1)

    assert table.innerJoin(beans2) == beans(
        [key1: 2, key2: 3, value: "aa", value2: "bb"],
        [key1: 3, key2: 4, value: "aaa", value2: "bbb"],
    )
  }

  @Test void shouldDoInnerJoin_UsingClosure() {
    def beans1 = beans(
        [key: "a", value: 1],
        [key: "b", value: 2]
    )
    def beans2 = beans(
        [key2: "a", value2: 3],
        [key2: "b", value2: 4])
    def table = new BeanTable(["key"], beans1)

    def actual = table.innerJoin(beans2) { thisBean, thatBean ->
      thisBean.key == thatBean.key2
    }
    assert actual == beans(
        [key: "a", key2: "a", value: 1, value2: 3],
        [key: "b", key2: "b", value: 2, value2: 4],
    )
  }

  @Test void shouldDoInnerJoin_WithAccumulation() {
    def beans1 = (1..5).collect {bean(key1: it, key2: it + 1, value: "a" * it)}
    def beans2 = (2..3).collect {bean(key1: it, key2: it + 1, value: "b" * it, value2: "c")}
    def table = new BeanTable(["key1", "key2"], beans1)
    table.whenBeanExists { oldBean, newBean ->
      oldBean.value += newBean.value
    }

    assert table.innerJoin(beans2) == beans(
        [key1: 2, key2: 3, value: "aabb", value2: "c"],
        [key1: 3, key2: 4, value: "aaabbb", value2: "c"]
    )
  }

  @Test void shouldDoInnerJoin_UsingCallbacksForMissingValues() {
    fail // TODO
  }

  @Test void shouldSelectValues() {
    def table = new BeanTable(["key"])
    table.insertAll(beans([key: 1, value: "a"], [key: 2, value: "b"], [key: 3, value: "c"]))

    assert table.select {it.key > 2} == beans([key: 3, value: "c"])
  }

  @Test void shouldDeleteValues() {
    def table = new BeanTable(["key"])
    table.insertAll(beans([key: 1, value: "a"], [key: 2, value: "b"], [key: 3, value: "c"]))
    table.delete {it.key >= 2}

    assert table.selectAll() == beans([key: 1, value: "a"])
  }
}
