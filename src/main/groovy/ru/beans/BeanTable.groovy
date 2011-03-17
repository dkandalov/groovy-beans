package ru.beans

/**
 * User: dima
 * Date: 10/3/11
 */
class BeanTable {
  private Map<List, Bean> beans
  private List<String> keys
  private Closure accumulationClosure

  BeanTable(def keys) {
    this(keys, [])
  }

  BeanTable(def keys, Collection beans) {
    this.beans = new LinkedHashMap()
    this.keys = keys

    insert(beans)
  }

  def whenBeanExists(Closure closure) {
    accumulationClosure = closure
  }

  def selectAll() {
    beans.values().toList()
  }

  def insert(Collection<Bean> beans) {
    beans.each {insert(it)}
  }

  def insert(Bean bean) {
    def key = bean.fieldValues(keys)
    if (accumulationClosure != null && beans.containsKey(key)) {
      accumulationClosure(beans.get(key), bean)
    } else {
      beans.put(key, bean)
    }
  }

  def join(Collection<Bean> beansToJoin) {
    beansToJoin.collect {
      def bean = beans.get(it.fieldValues(keys))
      bean?.mergeWith(it)
    }.findAll {it != null}
  }
}
