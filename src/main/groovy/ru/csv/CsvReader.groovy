package ru.csv

import ru.beans.Bean

 /**
 * Reads {@link Bean}s from csv file.
 *
 * Should:
 *  + read file in one go
 *  + read file line by line
 *    - it would be useful to have "streams" of data and be able to sequence them in functional style
 *
 *  + read all columns into beans as is
 *  + read subset of columns
 *
 *  - smart reader: should guess field types according to file content (try all date formats, integer, double, string)
 *
 *  - detect when file content doesn't match "beanType"
 *    - nice reader: fill fields that match
 *    - strict reader: fail fast
 *
 * User: dima
 * Date: 9/2/11
 */
class CsvReader {

  def header
  def beanType = [:]
  List columnsToRead = []
  Map columnMapping = new LinkedHashMap()

  CsvReader withBeanType(def beanType) {
    this.beanType = beanType
    this
  }

  CsvReader usingColumns(List columnsToRead) {
    this.columnsToRead = columnsToRead
    this
  }

  List<Bean> readString(String csvString) {
    read(new StringReader(csvString))
  }

  List<Bean> read(String fileName) {
    read(new FileReader(fileName))
  }

  List<Bean> read(Reader inputReader) {
    def result = []
    readEachLine(inputReader) { result << it }
    result
  }

  def readEachLine(String fileName, Closure closure) {
    readEachLine(new FileReader(fileName), closure)
  }

  def readEachLine(Reader inputReader, Closure closure) {
    inputReader.eachLine { line, i ->
      if (i == 1) {
        header = readHeader(line) // TODO throw exception if this line is not what expected (like empty line)
        prepareHeaderMapping()
      } else {
        closure.call(readBean(line))
      }
    }
  }

  private def readBean(String s) {
    def values = splitIntoValues(s)
    if (values.size() < header.size()) throw new IllegalStateException("Too few values in line \"${s}\"")
    if (values.size() > header.size()) throw new IllegalStateException("Too many values in line \"${s}\"")

    def map = [:]
    columnMapping.each {
      map.put(it.key, values[it.value])
    }
    new Bean(map, beanType)
  }

  static def splitIntoValues(String s) {
    (s =~ /"(.*?)",|"(.*)"$|(.*?),|(?<=,)(.*)$/)
            .collect { it[1..4].find {it != null}.replaceAll("\"\"", "\"") }
  }

  private def readHeader(String s) {
    s.split(",").toList()
  }

  private def prepareHeaderMapping() {
    if (!header.containsAll(columnsToRead)) throw new IllegalStateException()

    columnMapping = new LinkedHashMap()
    header.eachWithIndex {columnName, columnIndex -> columnMapping.put(columnName, columnIndex) }

    if (!columnsToRead.empty) {
      columnMapping = columnMapping.findAll { columnsToRead.contains(it.key) }
    }
  }
}
