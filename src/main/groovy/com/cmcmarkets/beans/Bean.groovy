package com.cmcmarkets.beans

/**
 * Storage for data with behavior of {@link Map} and syntax of an object.
 * It's like {@link Expando} but with a bit more functionality.
 *
 *
 * Eventually it will probably support:
 *  + nice bean: allows to read/write any property
 *  - strict bean: only allows to read/write properties from "beanType"
 *
 *  + be able to diff beans
 *  - be able to do sql-like operations on beans
 *
 * User: dima
 * Date: 8/2/11
 */
class Bean {
  private def data = [:]
  private def beanType = [:]

  /**
   * @data
   * @return collection of beans created from {@code data}
   */
  static Collection<Bean> beans(Collection<Map> data) {
    data.collect{ new Bean(it) }
  }

  /**
   * @data
   * @return collection of beans created from {@code data}
   */
  static Collection<Bean> beans(Map... data) {
    data.collect{ new Bean(it) }
  }

  /**
   * @return new empty {@link Bean}
   */
  static Bean bean() {
    bean([:])
  }

  /**
   * @data
   * @return {@link Bean} with values copied from {@data}
   */
  static Bean bean(Map data) {
    new Bean(data)
  }

  /**
   * Creates new empty bean.
   */
  Bean() {
  }

  /**
   * Copy constructor.
   * @param bean bean to copy data from
   */
  Bean(Bean bean) {
    this(bean.@data)
  }

  /**
   * @param data a {@link Map} to copy data from
   */
  Bean(Map data) {
    this(data, [:])
  }

  /**
   * @param data a {@link Map} to copy data from
   * @param beanType a {@link Map} which associates field names with {@link BeanType}s
   * @see #withType(Object)
   */
  Bean(Map data, Map  beanType) {
    this.@data = new LinkedHashMap(data) // must be linked map to preserve columns order

    withType(beanType)
    if (!beanType.empty) {
      applyBeanTypeConversion(data)
    }
  }

  private def applyBeanTypeConversion(newData) {
    newData.each { setProperty(it.key, it.value) }
  }

  /**
   * Adds type information to this bean.
   * @param beanType a {@link Map} which associates field names with {@link BeanType}s
   */
  Bean withType(def beanType) {
    this.beanType = beanType
    eachValue { key, value -> setProperty(key, value) }
    this
  }

  /**
   * Modifies this bean by adding all fields from {@code that} bean.
   * It will overwrite all fields in this bean with same names unless there is {@code accumulationClosure}.
   *
   * @param that {@link Bean} to merge this bean with
   * @param accumulationClosure if present, should merge this bean fields which have the same name as {@code that} fields
   * @return this
   */
  Bean mergeWith(Bean that, Closure accumulationClosure = null) {
    if (accumulationClosure != null) {
      accumulationClosure(this, that)
      (that.fieldNames() - this.fieldNames()).each { this.@data.put(it, that.getProperty((String) it)) }
    } else {
      that.fieldNames().each { this.@data.put(it, that.getProperty((String) it)) }
    }
    this
  }

  def eachValue(Closure closure) {
    this.@data.each { closure.call(it.key, it.value) }
  }

  List fieldNames() {
    this.@data.keySet().toList()
  }

  List fieldValuesFor(List<String> fieldNames) {
    fieldNames.collect{ getProperty(it) }
  }

  @Override void setProperty(String propertyName, Object newValue) {
    if (beanType.containsKey(propertyName)) {
      try {
        this.@data[propertyName] = beanType[propertyName].convert(newValue)
      } catch (Exception e) {
        throw new IllegalStateException("Failed to set property \"${propertyName}\" to value \"${newValue}\"", e)
      }
    } else {
      this.@data[propertyName] = newValue
    }
  }

  @Override Object getProperty(String propertyName) {
    this.@data[propertyName]
  }

  /**
   * Modifies this bean by renaming field from {@code oldName} to {@code newName}.
   * @param oldName
   * @param newName
   * @return this
   */
  Bean renameField(String oldName, String newName) {
    def value = this.@data.remove(oldName)
    this.@data.put(newName, value)
    this
  }

  @Override boolean equals(o) {
    if (this.is(o)) return true;
    if (this.getClass() != o.getClass()) return false;

    Bean bean = (Bean) o;

    if (this.@data != bean.@data) return false;

    return true;
  }

  @Override int hashCode() {
    return (this.@data != null ? this.@data.hashCode() : 0);
  }

  @Override public String toString() {
    return "bean" + this.@data.toMapString()
  }
}
