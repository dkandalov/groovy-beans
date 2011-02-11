package ru

/**
 * User: dima
 * Date: 8/2/11
 */
class Bean {
  def data = [:]

  Bean() {
  }

  Bean(data) {
    this.data = data
  }

  @Override void setProperty(String property, Object newValue) {
    data[property] = newValue
  }

  @Override Object getProperty(String property) {
    data[property]
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
