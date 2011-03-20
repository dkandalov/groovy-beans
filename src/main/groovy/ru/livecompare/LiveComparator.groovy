package ru.livecompare

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

/**
 * User: dima
 * Date: 19/3/11
 */
class LiveComparator {
  final def deque1 = new LinkedBlockingDeque()
  final def deque2 = new LinkedBlockingDeque()

  def executor = Executors.newScheduledThreadPool(1)
  def listComparator = new ListComparator()

  def start(long period, TimeUnit timeUnit, Closure listener) {
    executor.scheduleAtFixedRate(new Runnable() {
      @Override void run() {
        def list1 = new ArrayList()
        def list2 = new ArrayList()
        deque1.drainTo(list1)
        deque2.drainTo(list2)

        def diff = listComparator.compare(list1, list2)
        if (diff != [:]) listener(diff)

        // TODO potentially more comparison

        if (!list1.empty) list1.each {deque1.addFirst(it)}
        if (!list2.empty) list2.each {deque2.addFirst(it)}

      }
    }, period, period, timeUnit)
    this
  }

  def add1(value) {
    deque1.addLast(value)
  }

  def add2(value) {
    deque2.addLast(value)
  }
}
