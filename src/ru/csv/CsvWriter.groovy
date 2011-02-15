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

  def write(String fileName, List<Bean> beans) {
    write(new FileWriter(fileName), beans)
  }

  def write(Writer writer, List beans) {
    if (beans.empty) return
    header = getHeaderFrom(beans)

    writer.withWriter { w ->
      w.append(headerAsString(header))
      beans.each { w.append(beanAsString(it)) }
    }
  }

  private String beanAsString(def bean) {
    header.collect { bean."$it" }.join(",") + "\n"
  }

  private String headerAsString(List header) {
    return header.join(",") + "\n"
  }

  private List getHeaderFrom(List beans) {
    beans[0].getFieldNames()
  }
}
