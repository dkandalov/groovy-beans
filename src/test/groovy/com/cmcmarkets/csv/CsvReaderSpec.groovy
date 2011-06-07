package com.cmcmarkets.csv

import com.cmcmarkets.beans.Bean
import com.cmcmarkets.beans.BeanType
import org.junit.Test
import static com.cmcmarkets.beans.Bean.*

 /**
 * User: dima
 * Date: 9/2/11
 */
class CsvReaderSpec {
  def shouldFail = new GroovyTestCase().&shouldFail

  @Test public void shouldReadBeansFromCorrectCsvText() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4,5.0
""")
    def reader = new CsvReader()
    assert reader.read(stringReader) == [
            new Bean([a: "A", b: "2", c: "3.0"]),
            new Bean([a: "AA", b: "4", c: "5.0"])
    ]
    assert reader.header == ["a", "b", "c"]
  }

  @Test public void shouldReadCsvLineByLine() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4,5.0
""")
    def reader = new CsvReader()

    def actual = []
    reader.readEachLine(stringReader) { bean -> actual << bean }
    assert actual == [
            new Bean([a: "A", b: "2", c: "3.0"]),
            new Bean([a: "AA", b: "4", c: "5.0"])
    ]
  }

  @Test public void shouldReadCsvWithMissingValues() {
    def reader = new CsvReader()
    def csvFile = { s -> new StringReader("a,b,c\n$s") }

    assert reader.read(csvFile("1,,")) == [new Bean([a: "1", b: "", c: ""])]
    assert reader.read(csvFile(",2,")) == [new Bean([a: "", b: "2", c: ""])]
    assert reader.read(csvFile(",,3")) == [new Bean([a: "", b: "", c: "3"])]

    assert reader.read(csvFile(",2,3")) == [new Bean([a: "", b: "2", c: "3"])]
    assert reader.read(csvFile("1,,3")) == [new Bean([a: "1", b: "", c: "3"])]
    assert reader.read(csvFile("1,2,")) == [new Bean([a: "1", b: "2", c: ""])]

    assert reader.read(csvFile(",,")) == [new Bean([a: "", b: "", c: ""])]
    assert reader.read(csvFile("1,2,3")) == [new Bean([a: "1", b: "2", c: "3"])]
  }

  @Test public void shouldThrowExceptionIfSomeValuesAreMissing() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4
""")
    def reader = new CsvReader()
    shouldFail(IllegalStateException.class) {
      reader.read(stringReader)
    }
  }

  @Test public void shouldThrowExceptionIfTooManyValues() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4,5.0,
""")
    def reader = new CsvReader()
    shouldFail(IllegalStateException.class) {
      reader.read(stringReader)
    }
  }

  @Test public void shouldUseBeanType() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4,5.0
""")
    def reader = new CsvReader().withBeanType([a: BeanType.STRING, b: BeanType.INTEGER, c: BeanType.DOUBLE])
    def actual = reader.read(stringReader)
    assert actual == [
            new Bean([a: "A", b: 2, c: 3.0d]),
            new Bean([a: "AA", b: 4, c: 5.0d])
    ]
  }

  @Test public void shouldReadSubsetOfColumnFromCsvFile() {
    def stringReader = new StringReader("""a,b,c
A,2,3.0
AA,4,5.0
""")
    def reader = new CsvReader().usingColumns(["a", "c"]).withBeanType([a: BeanType.STRING, b: BeanType.INTEGER, c: BeanType.DOUBLE])
    def actual = reader.read(stringReader)
    assert actual == [
            new Bean([a: "A", c: 3.0d]),
            new Bean([a: "AA", c: 5.0d])
    ]
  }

  @Test public void shouldUnEscapeQuotesAndCommas() {
    def stringReader = new StringReader("""a,b,c
1,"2,2",3
"1,1",2,3
1,"2,2","3,3"
1,""2"",3
""")
    assert new CsvReader().read(stringReader) == beans(
            [a: "1", b: "2,2", c: "3"],
            [a: "1,1", b: "2", c: "3"],
            [a: "1", b: "2,2", c: "3,3"],
            [a: "1", b: "\"2\"", c: "3"]
    )
  }

  @Test public void shouldUnEscapeQuotesInHeader() {
    def stringReader = new StringReader("""\"a\",\"b b\",\"c\"
1,2,3
""")
    assert new CsvReader().read(stringReader) == beans([a: "1", "b b": "2", c: "3"])
  }

  @Test public void shouldSplitStringIntoValues() {
    assert CsvReader.splitIntoValues("1") == ["1"]
    assert CsvReader.splitIntoValues("1,2,") == ["1", "2", ""]
    assert CsvReader.splitIntoValues("1,2,3") == ["1", "2", "3"]

    assert CsvReader.splitIntoValues("\"1,1\",2,3") == ["1,1", "2", "3"]
    assert CsvReader.splitIntoValues("1,\"2,2\",3") == ["1", "2,2", "3"]
    assert CsvReader.splitIntoValues("1,2,\"3,3\"") == ["1", "2", "3,3"]

    assert CsvReader.splitIntoValues("0,q\"\"1\"\"q") == ["0", "q\"1\"q"]
    assert CsvReader.splitIntoValues("q\"\"1\"\"q,2,3") == ["q\"1\"q", "2", "3"]
    assert CsvReader.splitIntoValues("\"QuotesNcomma!,\"\"1\"\"q\",2,3") == ["QuotesNcomma!,\"1\"q", "2", "3"]
  }
}
