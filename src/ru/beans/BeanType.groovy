package ru.beans

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * User: dima
 * Date: 13/2/11
 */
class BeanType {
  private static def typeMapping = [:]

  static def STRING = new Convertor("STRING", {
    put String.class, { it }
    put Integer.class, { it.toString() }
    put BigDecimal.class, { it.toString() }
    put Double.class, { it.toString() }
  })

  static def DATE_AS_STRING(def format) {
    new Convertor("DATE_AS_STRING_$format", {
      put Date.class, { new SimpleDateFormat(format).format(it) }
    })
  }

  static def INTEGER = new Convertor("INTEGER", {
    put String.class, { Integer.parseInt(it) }
    put Integer.class, { it }
    put BigDecimal.class, { throw new IllegalStateException() }
    put Double.class, { throw new IllegalStateException() }
  })

  static def DOUBLE = new Convertor("DOUBLE", {
    put Double.class, { it }
    put Integer.class, { it }
    put String.class, { Double.parseDouble(it) }
    put BigDecimal.class, { throw new IllegalStateException() }
  })

  static def DATE(def format) {
    def dateTypeId = "DATE_${format}"

    new Convertor(dateTypeId, {
      put String.class, {
        try {
          new SimpleDateFormat(format).parse(it)
        } catch (ParseException e) {
          new Date(Long.parseLong(it))
        }
      }
      put Long.class, {new Date((long) it)}
    })
  }

  public static class Convertor {
    def convertTo

    Convertor(convertTo) {
      this.convertTo = convertTo
    }

    Convertor(convertTo, Closure closure) {
      this.convertTo = convertTo
      closure.delegate = this
      closure.call()
    }

    def put(Class srcType, Closure closure) {
      if (!typeMapping.containsKey(srcType)) {
        typeMapping.put(srcType, [:])
      }
      typeMapping[srcType].put(convertTo, closure)
    }

    def convert(def value) {
      def mapping = typeMapping[value.getClass()]
      if (mapping == null) throw new IllegalStateException("There is no mapping from type ${value.getClass()}. (Value: \"${value}\")")

      def closure = mapping.get(convertTo)
      if (closure == null) throw new IllegalStateException("There is no mapping to type ${convertTo} from ${value.class.simpleName} (value: \"${value}\")")

      closure.call(value)
    }

    @Override public String toString() {
      convertTo.toString()
    }
  }
}
