package ru.beans

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanListDiff {
  static BeanListDiff diff(Collection<Bean> beans1, Collection<Bean> beans2,
                           List keyFields, List fieldsToCompare) {
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
      def differentFields = fieldsToCompare.findAll { field ->
        beansForKey1[0]."${field}" != beansForKey2[0]."${field}"
      }
      if (!differentFields.empty) {
        diff << new BeanDiff([], differentFields, [], beansForKey1[0], beansForKey2[0])
      }
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

  public String toString() {
    return "BeanListDiff{" +
            "left=" + left +
            ", diff=" + diff +
            ", right=" + right +
            '}';
  }
}
