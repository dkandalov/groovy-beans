package com.cmcmarkets.livecompare

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

/**
 * User: dima
 * Date: 19/3/11
 */
class DataSequenceComparator {
  final def deque1 = new LinkedBlockingDeque()
  final def deque2 = new LinkedBlockingDeque()

  def executor = Executors.newScheduledThreadPool(1)
  def listComparator = new ListComparator()

  static compareStreams(stream1, stream2, long period = 3, TimeUnit timeUnit = TimeUnit.SECONDS) {
    def comparator = new DataSequenceComparator().startComparingEvery(period, timeUnit) { diff, consumedFrom1, consumedFrom2, remainingIn1, remainingIn2 ->
      if (diff.size() > 0) {
        println "===================================================== ${new Date()}"
        println diff
        println "====================================================="
      }
      println "consumed: ${consumedFrom1}, ${consumedFrom2}; remaining: ${remainingIn1.size()}, ${remainingIn2.size()}"
    }
    stream1.onEvent{ comparator.add1(it) }
    stream2.onEvent{ comparator.add2(it) }
  }

  static asStream(Closure onInput, Closure convert) {
    def callback = null
    onInput { input ->
      if (callback == null) return
      def event = convert(input)
      if (event != null) callback(event)
    }
    new Expando([onEvent: { callback = it }])
  }

  def startComparingEvery(long period, TimeUnit timeUnit, Closure listener) {
    executor.scheduleAtFixedRate(new Runnable() {
      @Override void run() {
        try {
          def list1 = new ArrayList()
          def list2 = new ArrayList()
          deque1.drainTo(list1)
          deque2.drainTo(list2)

          def list1Size = list1.size()
          def list2Size = list2.size()
          def diff = listComparator.compare(list1, list2)

          def consumedFrom1 = list1Size - list1.size()
          def consumedFrom2 = list2Size - list2.size()
          listener(diff, consumedFrom1, consumedFrom2, list1, list2)

          if (!list1.empty) list1.each {deque1.addFirst(it)}
          if (!list2.empty) list2.each {deque2.addFirst(it)}
        } catch (Exception e) {
          e.printStackTrace()
        }

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
