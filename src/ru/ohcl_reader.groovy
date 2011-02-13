package ru

import static BeanType.*

def ohlcType = [
        date: DATE("dd/MM/yyyy"),
        instrument: STRING,
        open: DOUBLE,
        high: DOUBLE,
        low: DOUBLE,
        close: DOUBLE
]
new CsvReader().withBeanType(ohlcType).readEachLine("/Users/dima/IdeaProjects/groovy-beans/data/Quotes.csv") {
  println it
}
