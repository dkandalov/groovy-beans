package ru.beans

import org.junit.Test
import static ru.beans.Bean.bean
import static ru.beans.Bean.beans

/**
 * User: dima
 * Date: 16/3/11
 */
class BeanTableSpec {
  @Test public void shouldInsertValuesIntoTable() {
    def table = new BeanTable(["key"])
    table.insert(bean([key: 1, value: "a"]))
    table.insert(beans([key: 2, value: "b"], [key: 3, value: "c"]))

    assert table.selectAll() == beans([key: 1, value: "a"], [key: 2, value: "b"], [key: 3, value: "c"])
  }

  @Test public void shouldReplaceExistingBeansWithNewValues_IfKeysAreTheSame() {
    def table = new BeanTable(["key"])
    table.insert(bean([key: 1, value: "a"]))
    table.insert(bean([key: 2, value: "b"]))
    table.insert(bean([key: 1, value: "A"]))

    assert table.selectAll() == beans([key: 1, value: "A"], [key: 2, value: "b"])
  }

  @Test public void shouldAccumulateBeans_IfTableHasAccumulationClosure() {
    def table = new BeanTable(["key"])
    table.whenBeanExists { oldBean, newBean -> oldBean.value += newBean.value }

    table.insert(bean([key: "a", value: 1]))
    table.insert(bean([key: "a", value: 2]))
    table.insert(bean([key: "b", value: 2]))

    assert table.selectAll() == beans([key: "a", value: 3], [key: "b", value: 2])
  }

  @Test public void shouldDoLeftJoin() {
    def beans1 = (1..5).collect {bean(key1: it, key2: it + 1, value: "a" * it)}
    def beans2 = (2..3).collect {bean(key1: it, key2: it + 1, value2: "b" * it)}
    def table = new BeanTable(["key1", "key2"], beans1)

    assert table.join(beans2) == beans(
        [key1: 2, key2: 3, value: "aa", value2: "bb"], [key1: 3, key2: 4, value: "aaa", value2: "bbb"]
    )
  }

  @Test public void shouldDoLeftJoin_WithAccumulation() {
    def beans1 = (1..5).collect {bean(key1: it, key2: it + 1, value: "a" * it)}
    def beans2 = (2..3).collect {bean(key1: it, key2: it + 1, value: "b" * it)}
    def table = new BeanTable(["key1", "key2"], beans1)
    table.whenBeanExists { oldBean, newBean ->
      oldBean.value += newBean.value
    }

    assert table.join(beans2) == beans(
        [key1: 2, key2: 3, value: "aabb"], [key1: 3, key2: 4, value: "aaabbb"]
    )
  }
}
