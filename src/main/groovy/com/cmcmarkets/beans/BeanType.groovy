package com.cmcmarkets.beans

import java.text.ParseException
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.NullObject

/**
 * It was supposed to be something like type for bean fields.
 * But didn't have to be more than a bunch of converters.
 *
 * User: dima
 * Date: 13/2/11
 */
class BeanType {

  static def STRING = new Convertor("STRING", {
    put String.class, { it }
    put Boolean.class, { it.toString() }
    put Integer.class, { it.toString() }
    put BigDecimal.class, { it.toString() }
    put Double.class, { it.toString() }
  })

  static def DATE_AS_STRING(def format) {
    new Convertor("DATE_AS_STRING_$format", {
      put Date.class, { new SimpleDateFormat(format).format(it) }
    })
  }

  static def BOOLEAN = new Convertor("BOOLEAN", {
    put String.class, { Boolean.parseBoolean(it) }
    put Boolean.class, { it }
    put Integer.class, { throw new IllegalStateException() }
    put BigDecimal.class, { throw new IllegalStateException() }
    put Double.class, { throw new IllegalStateException() }
  })

  static def BOOLEAN_LENIENT = new Convertor("BOOLEAN_LENIENT", {
    put String.class, { Boolean.parseBoolean(it) }
    put Boolean.class, { it }
    put Integer.class, { it != 0 }
    put BigDecimal.class, { it != 0 }
    put Double.class, { it != 0.0d }
    put NullObject.class, { false }
  })

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

  static def DECIMAL = new Convertor("DECIMAL", {
    put Double.class, { new BigDecimal((double) it) }
    put Integer.class, { new BigDecimal((int) it) }
    put String.class, { new BigDecimal((String) it) }
    put BigDecimal.class, { it }
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
    private Map<Class, Closure> typeMapping = [:]

    Convertor(String convertTo, Closure closure) {
      this.convertTo = convertTo
      closure.delegate = this
      closure.call()
    }

    def put(Class srcType, Closure closure) {
      typeMapping.put(srcType, closure)
    }

    def convert(def value) {
      def closure = typeMapping[value.getClass()]
      if (closure == null) {
        throw new IllegalStateException("There is no mapping from type ${value.getClass()} to ${convertTo}. (Value: \"${value}\")")
      }

      closure.call(value)
    }

    @Override public String toString() {
      convertTo.toString()
    }
  }
}
