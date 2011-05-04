package ru

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory

/**
 * User: dima
 * Date: 13/2/11
 */
class Util {
  static Double abs(def value) {
    if (value instanceof Double) {
      Math.abs((double) value)
    } else if (value instanceof Integer) {
      Math.abs((int) value)
    } else if (value instanceof Long) {
      Math.abs((long) value)
    } else {
      throw new IllegalStateException("Could use Math.abs() for value ${value} of type ${value.class}")
    }
  }

  static Date date(int dayOfMonth, int month, int year) {
    def calendar = Calendar.instance
    calendar.with {
      set(Calendar.DAY_OF_MONTH, dayOfMonth)
      set(Calendar.MONTH, month - 1)
      set(Calendar.YEAR, year)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    calendar.getTime()
  }

  static Collection extractFrom(Collection collection, Closure shouldExtract) { // TODO add to GDK more dynamically
    def result = []
    def i = collection.iterator()
    while (i.hasNext()) {
      def next = i.next()
      if (shouldExtract(next)) {
        result << next
        i.remove()
      }
    }
    result
  }

  static <T> Future<T> async(T defaultValue = null, Closure closure) {
    def executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory())
    executor.submit(new Callable() {
      @Override
      Object call() {
        try {
          closure.call()
        } catch (Exception e) {
          e.printStackTrace()
          defaultValue
        }
      }
    })
  }

  public static class DaemonThreadFactory implements ThreadFactory {
    @Override
    Thread newThread(Runnable r) {
      def t = new Thread(r)
      t.daemon = true
      return t
    }
  }
}
