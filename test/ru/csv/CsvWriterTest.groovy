package ru.csv

import org.junit.Test
import static ru.beans.Bean.beans

 /**
 * User: dima
 * Date: 15/2/11
 */
class CsvWriterTest {

  @Test public void shouldWriteEmptyCsvForEmptyInput() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, [])
    assert csv.toString() == ""
  }

  @Test public void shouldWriteCollectionOfBeansToCsv() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """a,b,c
A,2,3.0
B,4,5.0
"""
  }

  @Test public void shouldWriteToCsvUnionOfAllBeansFields() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, beans([a: "A"], [b: 4], [c: 3.0]))

    assert csv.toString() == """a,b,c
A,,
,4,
,,3.0
"""
  }

  @Test public void shouldBeAbleToWriteSubsetOfBeanFields() {
    def csv = new StringWriter()
    new CsvWriter().usingFields(["a", "c"]).writeTo(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """a,c
A,3.0
B,5.0
"""
  }

  @Test public void shouldBeanFieldsInParticularOrder() {
    def csv = new StringWriter()
    new CsvWriter().usingOrder(["c", "a"]).writeTo(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """c,a,b
3.0,A,2
5.0,B,4
"""
  }
}
