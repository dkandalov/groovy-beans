package ru

/**
 * User: dima
 * Date: 9/2/11
 */
class CsvReader {

  def header

  List read(Reader inputReader) {
    def result = []
    inputReader.eachLine { line, i ->
      if (i == 1) {
        header = readHeader(line) // TODO throw exception if this line is not what expected (like empty line)
      } else {
        result << readBean(line)
      }
    }
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
    new Bean(map)
  }

  def readHeader(String s) {
    header = s.split(",")
  }
}
