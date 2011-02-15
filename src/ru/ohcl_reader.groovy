package ru

import ru.jfchart.JFC_Playground
import static ru.beans.BeanType.*
import ru.csv.CsvReader

def quotesFile = "/Users/dima/IdeaProjects/groovy-beans/data/Quotes.csv"
def ohlcType = [
        date: DATE("dd/MM/yyyy"),
        instrument: STRING,
        open: DOUBLE,
        high: DOUBLE,
        low: DOUBLE,
        close: DOUBLE
]

def collectt(Closure closure) {
  def result = []
  return {
    if (closure.call(it)) result << it
    result
  }
}

private static class SimpleAverage {
  double sum
  def data = new LinkedList()
  int avgSize

  SimpleAverage(int avgSize) {
    this.avgSize = avgSize
  }

  def leftShift(double value) {
    data.addLast(value)
    if (data.size() > avgSize) {
      sum = sum - data.removeFirst()
    }
    sum += value
  }

  double getValue() {
    sum / avgSize
  }

  boolean hasValue() {
    data.size() >= avgSize
  }
}

def series = JFC_Playground.showBeans()
def average = new SimpleAverage(100)
new CsvReader().withBeanType(ohlcType).readEachLine(quotesFile) {
  if (it.instrument == "AA") {
    average.leftShift(it.open)
    if (average.hasValue()) {
      series[1].add((long) it.date.time, (double) average.getValue())
    }

    series[0].add((long) it.date.time, (double) it.open)
//    series[1].add((long) it.date.time, (double) it.open + 3)
  }
}