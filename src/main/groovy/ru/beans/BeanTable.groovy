package ru.beans

/**
 * User: dima
 * Date: 10/3/11
 */
class BeanTable {
  List<Bean> beans

  BeanTable(List<Bean> beans, List keyFields) {
    this.beans = beans
  }

  def select(Closure closure) {
    beans.collect(closure)
  }

}
