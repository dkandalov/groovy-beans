package ru.beans

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanDiff {
  static def diff(Bean bean1, Bean bean2) {
    def commonFields = bean1.fieldNames()
    commonFields.retainAll(bean2.fieldNames())
    def diff = commonFields.findAll { fieldName ->
      bean1."${fieldName}" != bean2."${fieldName}"
    }

    def left = bean1.fieldNames() - bean2.fieldNames()
    def right = bean2.fieldNames() - bean1.fieldNames()

    [left, diff, right] as BeanDiff
  }

  def left
  def diff
  def right

  BeanDiff(left, diff, right) {
    this.left = left
    this.diff = diff
    this.right = right
  }

  boolean match() {
    diff.empty && left.empty && right.empty
  }


  public String toString ( ) {
  return "BeanDiff{" +
  "diff=" + diff +
  ", left=" + left +
  ", right=" + right +
  '}' ;
  }}
