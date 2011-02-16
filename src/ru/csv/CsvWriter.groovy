package ru.csv

import ru.beans.Bean

 /**
 * Reads {@link ru.beans.Bean}s from csv file.
 *
 * Should:
 *  - write to file a collection of beans
 *  - write to file bean after bean
 *
 *  - write beans as is to .csv file
 *    - active writer: get csv header as a union of all possible fields in beans list
 *    - passive writer: get csv header as fields from the first bean
 *    - instructed writer: writes specified subset of fields
 *  - write bean fields in particular order
 *  - write beans using specific convertors (e.g. date convertors)
 *
 *  - detect when beans don't match header
 *    - nice writer: adds empty string
 *    - strict writer: fail fast
 *
 *  - append to existing .csv file (i.e. read file header and write beans accordingly)
 *
 * User: dima
 * Date: 15/2/11
 */
class CsvWriter {
  private def header = []
  private def fieldsToWrite = []

  def writeTo(String fileName, List<Bean> beans) {
    writeTo(new FileWriter(fileName), beans)
  }

  def writeTo(Writer writer, List beans) {
    if (beans.empty) return
    header = getHeaderFrom(beans)

    writer.withWriter { w ->
      w.append(headerAsString(header))
      beans.each { w.append(beanAsString(it)) }
    }
  }

  CsvWriter usingFields(List<String> fieldNames) {
    fieldsToWrite = new ArrayList(fieldNames)
    this
  }

  CsvWriter usingOrder(List<String> fieldNames) {
    this
  }

  private String beanAsString(def bean) {
    header.collect {
      def value = bean."$it"
      (value == null ? "" : value) // I don't think anyone ever need "null" values in .csv file
    }.join(",") + "\n"
  }

  private String headerAsString(List header) {
    return header.join(",") + "\n"
  }

  private List getHeaderFrom(List beans) {
    if (!fieldsToWrite.empty) return fieldsToWrite

    def result = new LinkedHashSet()
    beans.each { bean ->
      bean.getFieldNames().each { result.add(it) }
    }
    result.toList()
  }
}
