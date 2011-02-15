package ru.beans

import java.text.SimpleDateFormat

/**
 * User: dima
 * Date: 13/2/11
 */
class BeanType {
  static def STRING = new Convertor("STRING")
  static def INTEGER = new Convertor("INTEGER")
  static def DOUBLE = new Convertor("DOUBLE")

  static def DATE(def format) {
    def dateTypeId = "DATE_${format}"

    new Convertor(dateTypeId, { typeMapping ->
      typeMapping[String.class] << ["$dateTypeId": {new SimpleDateFormat(format).parse(it)}]
    })
  }

  private static def typeMapping = [:]

  static {
    typeMapping.with {
      put String.class, [
              STRING: {it},
              INTEGER: {Integer.parseInt(it)},
              DOUBLE: {Double.parseDouble(it)}
      ]

      put Integer.class, [
              STRING: {it.toString()},
              INTEGER: {it},
              DOUBLE: {it}
      ]

      put BigDecimal.class, [
              STRING: {it.toString()},
              INTEGER: {throw new IllegalStateException()},
              DOUBLE: {throw new IllegalStateException()}
      ]

      put Double.class, [
              STRING: {it.toString()},
              INTEGER: {throw new IllegalStateException()},
              DOUBLE: {it}
      ]
    }
  }

  public static class Convertor {
    def convertTo

    Convertor(convertTo) {
      this.convertTo = convertTo
    }

    Convertor(convertTo, Closure closure) {
      this.convertTo = convertTo
      closure.call(typeMapping)
    }

    def convert(def value) {
      def mapping = typeMapping[value.getClass()]
      if (mapping == null) throw new IllegalStateException("There is no mapping from type ${value.getClass()}. (Value: \"${value}\")")

      def closure = mapping.get(convertTo)
      if (closure == null) throw new IllegalStateException("There is no mapping to type ${convertTo}. (Value: \"${value}\")")

      closure.call(value)
    }


    @Override public String toString() {
      convertTo.toString()
    }
  }
}
