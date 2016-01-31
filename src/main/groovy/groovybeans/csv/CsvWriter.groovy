package groovybeans.csv

import groovybeans.beans.Bean

/**
 * Reads {@link groovybeans.beans.Bean}s from csv file.
 *
 * Should:
 *  + write to file collection of beans
 *  - write to file bean after bean
 *
 *  + write beans as is to .csv file
 *    - active writer: get csv header as a union of all possible fields in beans list
 *    - passive writer: get csv header as fields from the first bean
 *    -- (probably it should be done before writing using operations on beans) instructed writer: writes specified subset of fields
 *  + write bean fields in particular order
 *  + write beans using specific convertors (e.g. date convertors)
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
class CsvWriter implements Closeable {
  private List header = []
  private List fieldOrder = []
  private Map convertors = [:]
  private List enforcedHeader = []
  private String quote = ""

  private Writer writerToAppendTo
  private boolean isFirstAppend = true

  static def write(File file, def beans) {
    new CsvWriter().writeTo(new FileWriter(file), beans)
  }

  static def write(String filename, def beans) {
    new CsvWriter().writeTo(filename, beans)
  }

  static def appendTo(String filename) {
    appendTo(new BufferedWriter(new FileWriter(filename)))
  }

  static def appendTo(Writer writer) {
    def csvWriter = new CsvWriter()
    csvWriter.writerToAppendTo = writer
    Runtime.runtime.addShutdownHook { csvWriter.close() }
    csvWriter
  }

  CsvWriter withHeader(List<String> enforcedHeader) {
    this.enforcedHeader = enforcedHeader
    this
  }

  CsvWriter usingFieldOrder(List<String> fieldOrder) {
    this.fieldOrder = fieldOrder
    this
  }

  CsvWriter usingConvertors(Map convertors) {
    this.convertors = convertors
    this
  }

  CsvWriter withQuotes() {
    quote = '"'
    this
  }

  def append(Bean bean, flush = false) {
    if (isFirstAppend) {
      header = getHeaderFrom([bean])
      writerToAppendTo.append(headerAsString(header))
      isFirstAppend = false
    }
    writerToAppendTo.append(beanAsString(bean))
    if (flush) writerToAppendTo.flush()
  }

  // the reason "fileName" is not passed in constructor is that syntax "CsvWriter.writeTo(myFile, beans)" seems to be closer to human language
  def writeTo(String fileName, def beans) {
    writeTo(new BufferedWriter(new FileWriter(fileName)), beans) // TODO retry if file is locked (by Excel for example)?
  }

  def writeTo(Writer writer, def beans) {
    if (beans.empty) return
    if (enforcedHeader.empty) {
      header = getHeaderFrom(beans)
      header = fieldOrder + (header - fieldOrder)
    } else {
      header = enforcedHeader
    }

    writer.withWriter { w ->
      w.append(headerAsString(header))
      beans.each { w.append(beanAsString(it)) }
    }
  }

  private String beanAsString(def bean) {
    def rowValuesAsString = header.collect {
      def value = bean."$it"
      if (convertors.containsKey(it)) value = convertors.get(it).convert(value)
      asString(value)
    }

    def isQuoted = { it.startsWith(quote) && it.endsWith(quote) }
    def result = ""
    for (int i = 0; i < rowValuesAsString.size() - 1; i ++) {
      def s = rowValuesAsString[i]
      result += (isQuoted(s) ? s + "," : quote + s + quote + ",")
    }
    def last = rowValuesAsString.last()
    result + (isQuoted(last) ? last + "\n" : quote + last + quote + "\n")
  }

  private def asString(value) {
    if (value == null) return "" // I don't think anyone ever need "null" values in .csv file
    value = value.toString()
    value = value.replaceAll("\"", "\"\"")
    if (value.contains(",")) value = "\"$value\""
    value
  }

  private String headerAsString(List header) {
    return quote + header.join("${quote},${quote}") + "${quote}\n"
  }

  private List getHeaderFrom(Collection beans) {
    def header = new LinkedHashSet() // this is to make "order" in which header composed consistent
    beans.each { bean ->
      bean.fieldNames().each { header.add(it) }
    }.toList()
    header.toList()
  }

  void close() {
    writerToAppendTo?.close()
  }
}
