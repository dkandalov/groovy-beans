package ru.beans

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanDiff {
  static def diff(Bean bean1, Bean bean2) {
    def commonFields = bean1.fieldNames()
    commonFields.retainAll(bean2.fieldNames())

    diff(bean1, bean2, commonFields)
  }

  static def diff(Bean bean1, Bean bean2, List fieldsToCompare) {
    def diff = fieldsToCompare.findAll { fieldName ->
      bean1."${fieldName}" != bean2."${fieldName}"
    }

    def left = bean1.fieldNames() - bean2.fieldNames()
    def right = bean2.fieldNames() - bean1.fieldNames()

    [left, diff, right, bean1, bean2] as BeanDiff
  }

  def left
  def diff
  def right
  def bean1, bean2

  BeanDiff(left, diff, right, bean1, bean2) {
    this.left = left
    this.diff = diff
    this.right = right
    this.bean1 = bean1
    this.bean2 = bean2
  }

  boolean match() {
    diff.empty && left.empty && right.empty
  }

  @Override public String toString() {
    return "BeanDiff{" +
            "diff=" + diff +
            ", left=" + left +
            ", right=" + right +
            '}';
  }

  @Override boolean equals(o) {
    if (this.is(o)) return true;
    if (getClass() != o.class) return false;

    BeanDiff beanDiff = (BeanDiff) o;

    if (bean1 != beanDiff.bean1) return false;
    if (bean2 != beanDiff.bean2) return false;
    if (diff != beanDiff.diff) return false;
    if (left != beanDiff.left) return false;
    if (right != beanDiff.right) return false;

    return true;
  }

  @Override int hashCode() {
    int result;
    result = (left != null ? left.hashCode() : 0);
    result = 31 * result + (diff != null ? diff.hashCode() : 0);
    result = 31 * result + (right != null ? right.hashCode() : 0);
    result = 31 * result + (bean1 != null ? bean1.hashCode() : 0);
    result = 31 * result + (bean2 != null ? bean2.hashCode() : 0);
    return result;
  }
}
