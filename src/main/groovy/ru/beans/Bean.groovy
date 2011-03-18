package ru.beans

/**
 * A storage for any data. It's like {@link Expando} but with additional features.
 *
 * Should:
 *  + nice bean: allows to read/write any property
 *  - strict bean: only allows to read/write properties from "beanType"
 *
 *  + case sensitive/insensitive ("instrumentid" changes the same field as "InstrumentID")
 *  - how should it affect equality/hashcode?
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
  private def fieldNamesMap = [:]

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

  Bean(data) {
    this(data, [:])
  }

  Bean(Map data, Map beanType) {
    this.data = new LinkedHashMap(data) // must be linked map to preserve columns order
    initFieldNames(data)
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

  Bean mergeWith(Bean bean) {
    def newBean = new Bean()
    newBean.@data.putAll(this.@data)
    newBean.@data.putAll(bean.@data)
    newBean
  }

  Bean mergeWith(Bean bean, Closure accumulationClosure) {
    def newBean = new Bean(this.@data)
    accumulationClosure(newBean, bean)
    newBean
  }

  List fieldNames() {
    fieldNamesMap.values().toList()
  }

  List fieldValues(List<String> fieldNames) {
    fieldNames.collect {getProperty(it)}
  }

  @Override void setProperty(String propertyName, Object newValue) {
    propertyName = internalNameOf(propertyName)

    if (beanType.containsKey(propertyName)) {
      data[propertyName] = beanType[propertyName].convert(newValue)
    } else {
      data[propertyName] = newValue
    }
  }

  @Override Object getProperty(String propertyName) {
    data[internalNameOf(propertyName)]
  }

  private def initFieldNames(Map data) {
    fieldNamesMap = data.keySet().inject([:]) { Map namesMap, key ->
      namesMap.put(key.toLowerCase(), key) // TODO for some reason I couldn't reference outer class fields in this closure. Investigate.
      namesMap
    }
  }

  private def internalNameOf(String propertyName) {
    String mappedName = fieldNamesMap.get(propertyName.toLowerCase())
    mappedName != null ? mappedName : propertyName
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
