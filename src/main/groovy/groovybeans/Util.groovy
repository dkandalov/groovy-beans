package groovybeans

import groovybeans.beans.Bean
import groovybeans.csv.CsvReader
import groovybeans.csv.CsvWriter
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory

/**
 * User: dima
 * Date: 13/2/11
 */
class Util {

  private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy")

  static runSafely(Closure closure) {
    try {
      closure.call()
    } catch (Exception e) {
      e.printStackTrace()
    }
  }

  static List<Bean> grepCsv(String csvFileName, Closure isAccepted) {
    def beans = new CsvReader().read(csvFileName)
    beans = beans.findAll { isAccepted(it) }
    new CsvWriter().writeTo(csvFileName, beans)
    beans
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

  static Date parseDate(String s) {
    DEFAULT_DATE_FORMAT.parse(s)
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
