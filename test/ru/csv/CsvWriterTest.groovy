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
    new CsvWriter().write(csv, [])
    assert csv.toString() == ""
  }

  @Test public void shouldWriteCollectionOfBeansToCsv() {
    def csv = new StringWriter()
    new CsvWriter().write(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """a,b,c
A,2,3.0
B,4,5.0
"""
  }
}
