package groovybeans.beans

/**
 * User: dima
 * Date: 10/3/11
 */
class BeanTable {
  private Map<List, Bean> beans
  private List<String> keys

  private Closure accumulationClosure
  private Closure whenMissingInThisTable
  private Closure whenMissingInThatTable
  private Closure insertionClosure

  BeanTable(def keys) {
    this(keys, [])
  }

  BeanTable(def keys, Collection beans) {
    this.beans = new LinkedHashMap()
    this.keys = keys

    insertAll(beans)
  }

  def whenBeanDoesntExist(Closure closure) {
    insertionClosure = closure
  }

  def whenBeanExists(Closure closure) {
    accumulationClosure = closure
  }

  def whenMissingInThisTable(Closure closure) {
    whenMissingInThisTable = closure
  }

  def whenMissingInThatTable(Closure closure) {
    whenMissingInThatTable = closure
  }

  def select(Closure closure) {
    beans.values().findAll {closure(it)}
  }

  Collection<Bean> selectAll() {
    beans.values().toList()
  }

  def delete(Closure closure) {
    beans.entrySet().removeAll {closure(it.value)}
  }

  def insertAll(Collection<Bean> beans) {
    beans.each { insert(it) }
    this
  }

  def insert(Bean newBean) {
    def key = newBean.fieldValuesFor(keys)
    def oldBean = beans.get(key)

    if (oldBean != null) {
      if (accumulationClosure != null)
        oldBean.mergeWith(newBean, accumulationClosure)
      else
        beans.put(key, newBean)
    } else {
      if (insertionClosure != null) {
        insertionClosure(newBean)
        beans.put(key, newBean)
      } else {
        beans.put(key, newBean)
      }
    }

    this
  }

  Collection<Bean> innerJoin(Collection<Bean> beansToJoin) {
    def result = new LinkedList()
    for (def thatBean : beansToJoin) {
      def thisBean = beans.get(thatBean.fieldValuesFor(keys))
      if (thisBean == null) continue

      if (accumulationClosure != null)
        result << new Bean(thisBean?.mergeWith(thatBean, accumulationClosure))
      else
        result << new Bean(thisBean?.mergeWith(thatBean))
    }
    result
  }

  Collection<Bean> innerJoin(Collection<Bean> beansToJoin, Closure canJoin) {
    beansToJoin.collect { thatBean ->
      beans.values().collect { thisBean ->
        if (canJoin(thisBean, thatBean)) {
          if (accumulationClosure != null)
            new Bean(thisBean?.mergeWith(thatBean, accumulationClosure))
          else
            new Bean(thisBean?.mergeWith(thatBean))
        }
      }
    }.flatten().findAll { it != null }
  }
}
