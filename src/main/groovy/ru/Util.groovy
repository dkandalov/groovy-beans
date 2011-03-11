package ru

/**
 * User: dima
 * Date: 13/2/11
 */
class Util {
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
}
