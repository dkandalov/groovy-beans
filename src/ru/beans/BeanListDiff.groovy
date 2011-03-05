package ru.beans

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanListDiff {
  static BeanListDiff diff(Collection<Bean> beans1, Collection<Bean> beans2,
                           List keyFields, List fieldsToCompare) {
    diffWithComparator(beans1, beans2, keyFields, fieldsToCompare) {
      //noinspection GroovyAssignabilityCheck
      BeanDiff.diff(bean1, bean2, fieldsToCompare)
    }
  }

  static BeanListDiff diffWithComparator(Collection<Bean> beans1, Collection<Bean> beans2,
                                         List keyFields, List fieldsToCompare, Closure comparator) {
    def groupedBeans1 = beans1.groupBy { bean ->
      keyFields.collect { field -> bean."${field}"}
    }
    def groupedBeans2 = beans2.groupBy { bean ->
      keyFields.collect { field -> bean."${field}"}
    }
    List left = (groupedBeans1.keySet() - groupedBeans2.keySet()).collect {groupedBeans1.get(it)}.flatten()
    List right = (groupedBeans2.keySet() - groupedBeans1.keySet()).collect {groupedBeans2.get(it)}.flatten()

    List diff = []
    List commonKeys = groupedBeans1.keySet().intersect(groupedBeans2.keySet()).toList()
    commonKeys.each { key ->
      def beansForKey1 = new LinkedList(groupedBeans1.get(key))
      def beansForKey2 = new LinkedList(groupedBeans2.get(key))

      if (beansForKey1.size() != beansForKey2.size()) throw new IllegalStateException() // TODO
      if (beansForKey1.size() > 1 || beansForKey2.size() > 1) throw new IllegalStateException()

      comparator.delegate = new Expando([beans1: beans1, beans2: beans2,
              bean1: beansForKey1[0], bean2: beansForKey2[0],
              keyFields: keyFields, fieldsToCompare: fieldsToCompare])

      BeanDiff beanDiff = (BeanDiff) comparator.call()
      if (!beanDiff.match()) diff << beanDiff
    }

    [left, diff, right] as BeanListDiff
  }

  def left
  def diff
  def right

  BeanListDiff(left, diff, right) {
    this.left = left
    this.diff = diff
    this.right = right
  }

  boolean match() {
    diff.empty && left.empty && right.empty
  }

  @Override public String toString() {
    return "BeanListDiff{" +
            "left=" + left +
            ", diff=" + diff +
            ", right=" + right +
            '}';
  }
}
