package ru.beans

/**
 * A storage for any data. It's like {@link Expando} but with additional features.
 *
 * Should:
 *  - nice bean: allows to read/write any property
 *  - strict bean: only allows to read/write properties from "beanType"
 *
 *  - case sensitive/insensitive ("instrumentid" changes the same field as "InstrumentID")
 *
 *  - be able to diff beans
 *  - be able to do sql-like operations on beans
 *
 * User: dima
 * Date: 8/2/11
 */
class Bean {
  private def data = [:]
  private def beanType = [:]

  static List beans(Map... data) {
    data.collect {new Bean(it)}
  }

  static Bean bean(Map data) {
    new Bean(data)
  }

  Bean() {
  }

  Bean(data) {
    this(data, [:])
  }

  Bean(data, beanType) {
    this.data = data
    withType(beanType)

    if (!beanType.empty) {
      applyBeanTypeConversion(data)
    }
  }

  private def applyBeanTypeConversion(data) {
    data.each { setProperty(it.key, it.value) }
  }

  Bean withType(def beanType) {
    this.beanType = beanType
    this
  }

  List getFieldNames() {
    data.keySet().toList()
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
    return this.@data.toMapString()
  }
}
