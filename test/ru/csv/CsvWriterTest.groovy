package ru.csv

import org.junit.Test
import static ru.Util.date
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

  @Test public void shouldWriteToCsvUnionOfAllBeansFields_UsingEmptyStringForNulls() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, beans([a: "A"], [b: 4], [c: 3.0]))

    assert csv.toString() == """a,b,c
A,,
,4,
,,3.0
"""
  }

  @Test public void shouldWriteBeanFieldsInParticularOrder() {
    def csv = new StringWriter()
    def beans = beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0])
    new CsvWriter().usingOrder(["c", "a"]).writeTo(csv, beans)

    assert csv.toString() == """c,a,b
3.0,A,2
5.0,B,4
"""
  }

  @Test public void shouldWriteBeanValuesUsingConverters() {
    def csv = new StringWriter()
    def beans = beans([a: date(2011, 02, 17), b: 2, c: 3.0], [a: date(2010, 02, 20), b: 4, c: 5.0])
//    new CsvWriter().usingConvertors(["c" : BeanType.DATE(), "a"]).writeTo(csv, beans)

    assert csv.toString() == """c,a,b
3.0,A,2
5.0,B,4
"""
  }
}
