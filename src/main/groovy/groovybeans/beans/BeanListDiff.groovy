package groovybeans.beans

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * // TODO document
 *
 * User: dima
 * Date: 22/2/11
 */
class BeanListDiff { // TODO have pretty printer for diffs
  // TODO have option to compare the rest of the fields
  static BeanListDiff diff(Collection<Bean> beans1, Collection<Bean> beans2, List keyFields) {
    def fieldsToCompare = beans1[0].fieldNames() - keyFields
    diff(beans1, beans2, keyFields, fieldsToCompare)
  }

  static BeanListDiff diff(Collection<Bean> beans1, Collection<Bean> beans2, List keyFields, List fieldsToCompare) {
    diffWithComparator(beans1, beans2, keyFields, fieldsToCompare) {
      //noinspection GroovyAssignabilityCheck
      BeanDiff.diff(bean1, bean2, fieldsToCompare)
    }
  }

  static BeanListDiff diffWithComparator(Collection<Bean> beans1, Collection<Bean> beans2, List keyFields, Closure comparator) {
    def fieldsToCompare = beans1[0].fieldNames() - keyFields
    diffWithComparator(beans1, beans2, keyFields, fieldsToCompare, comparator)
  }

  static BeanListDiff diffWithComparator(Collection<Bean> beans1, Collection<Bean> beans2,
                                         List keyFields, List fieldsToCompare, Closure comparator) {
    def groupedBeans1 = beans1.groupBy { bean ->
      keyFields.collect { field -> bean."${field}" }
    }
    def groupedBeans2 = beans2.groupBy { bean ->
      keyFields.collect { field -> bean."${field}" }
    }
    List left = (groupedBeans1.keySet() - groupedBeans2.keySet()).collect {groupedBeans1.get(it)}.flatten()
    List right = (groupedBeans2.keySet() - groupedBeans1.keySet()).collect {groupedBeans2.get(it)}.flatten()

    List diff = []
    List commonKeys = groupedBeans1.keySet().intersect(groupedBeans2.keySet()).toList()
    commonKeys.each { key ->
      def beansForKey1 = new LinkedList(groupedBeans1.get(key))
      def beansForKey2 = new LinkedList(groupedBeans2.get(key))

      if (beansForKey1.size() != beansForKey2.size()) throw new IllegalStateException("There different amount of beans for keys: ${commonKeys}") // TODO
      if (beansForKey1.size() > 1) throw new IllegalStateException("There are several beans for key fields. Beans: ${beansForKey1}") // TODO provide a special case to exclude elements with similar keys?
      if (beansForKey2.size() > 1) throw new IllegalStateException("There are several beans for key fields. Beans: ${beansForKey2}")

      comparator.delegate = new Expando([
              beans1: beans1, beans2: beans2,
              bean1: beansForKey1[0], bean2: beansForKey2[0],
              keyFields: keyFields, fieldsToCompare: fieldsToCompare])

      BeanDiff beanDiff = (BeanDiff) comparator.call()
      if (!beanDiff.diff.empty) diff << beanDiff
    }

    new BeanListDiff(left, diff, right, keyFields)
  }

  // TODO I want to have more type information in client code
  // TODO  rename diff to "beanDiff" or something like that
  private Logger logger = LoggerFactory.getLogger(BeanListDiff.class)

  Collection left
  Collection<BeanDiff> diff
  Collection right
  Collection keyFields

  BeanListDiff(left, diff, right, keyFields) {
    this.left = left
    this.diff = diff
    this.right = right
    this.keyFields = keyFields
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

  def logDiff() {
    logger.info "===== diffs ====="
    diff.each { beanDiff ->
      logger.info "key: " + keyFields.collect { it + ": " + beanDiff.bean1."$it" } + " diff: " + beanDiff.diff.collect { it + " [" + beanDiff.bean1."$it" + ", " + beanDiff.bean2."$it" + "]"}
    }

    logger.info "===== left ====="
    left.each { bean -> logger.info keyFields.collect { it + ": " + bean."$it" }.toString() }

    logger.info "===== right ====="
    right.each { bean -> logger.info keyFields.collect { it + ": " + bean."$it" }.toString() }

    logger.info "diff size: ${diff.size()}"
    logger.info "left size: ${left.size()}"
    logger.info "right size: ${right.size()}"
  }

}
