package ru.beans

/**
 * User: dima
 * Date: 8/2/11
 */
class Bean {
  def data = [:]
  def beanType = [:]

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
