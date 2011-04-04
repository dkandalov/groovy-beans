package ru.beans

/**
 * A storage for any data. It's like {@link Expando} but with additional features.
 *
 * Should:
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

  static Collection beans(Collection<Map> data) {
    data.collect {new Bean(it)}
  }

  static Collection beans(Map... data) {
    data.collect {new Bean(it)}
  }

  static Bean bean() {
    bean([:])
  }

  static Bean bean(Map data) {
    new Bean(data)
  }

  Bean() {
  }

  Bean(Bean bean) {
    this(bean.data)
  }

  Bean(def data) {
    this(data, [:])
  }

  Bean(Map data, Map beanType) {
    this.data = new LinkedHashMap(data) // must be linked map to preserve columns order
    withType(beanType)

    if (!beanType.empty) {
      applyBeanTypeConversion(data)
    }
  }

  private def applyBeanTypeConversion(newData) {
    newData.each { setProperty(it.key, it.value) }
  }

  Bean withType(def beanType) {
    this.beanType = beanType
    this
  }

  Bean mergeWith(Bean that, Closure accumulationClosure = null) {
    (that.fieldNames() - this.fieldNames()).each { this.@data.put(it, that.getProperty((String) it)) }

    def commonFields = this.fieldNames().intersect(that.fieldNames())
    commonFields.each { this.@data.put(it, this.getProperty((String) it)) }

    if (accumulationClosure != null)  accumulationClosure(this, that)

    this
  }

  List fieldNames() {
    data.keySet().toList()
  }

  List fieldValuesFor(List<String> fieldNames) {
    fieldNames.collect {getProperty(it)}
  }

  @Override void setProperty(String propertyName, Object newValue) {
    if (beanType.containsKey(propertyName)) {
      data[propertyName] = beanType[propertyName].convert(newValue)
    } else {
      data[propertyName] = newValue
    }
  }

  @Override Object getProperty(String propertyName) {
    data[propertyName]
  }

  Bean renameField(String oldName, String newName) {
    def value = data.remove(oldName)
    data.put(newName, value)
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
