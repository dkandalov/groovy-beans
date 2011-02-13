package ru

/**
 * User: dima
 * Date: 9/2/11
 */
class CsvReader {

  def header
  def beanType = [:]

  CsvReader withBeanType(def beanType) {
    this.beanType = beanType
    this
  }

  List read(String fileName) {
    read(new FileReader(fileName))
  }

  def readEachLine(String fileName, Closure closure) {
    readEachLine(new FileReader(fileName), closure)
  }

  def readEachLine(Reader inputReader, Closure closure) {
    inputReader.eachLine { line, i ->
      if (i == 1) {
        header = readHeader(line) // TODO throw exception if this line is not what expected (like empty line)
      } else {
        closure.call(readBean(line))
      }
    }
  }

  List read(Reader inputReader) {
    def result = []
    readEachLine(inputReader) { result << it }
    result
  }

  def readBean(String s) {
    def values = s.split(",", -1)
    if (values.size() < header.size()) throw new IllegalStateException("Too few values in line \"${s}\"")
    if (values.size() > header.size()) throw new IllegalStateException("Too many values in line \"${s}\"")

    def map = [:]
    header.eachWithIndex { columnName, i ->
      map.put(columnName, values[i])
    }
    new Bean(map, beanType)
  }

  def readHeader(String s) {
    header = s.split(",")
  }

}
