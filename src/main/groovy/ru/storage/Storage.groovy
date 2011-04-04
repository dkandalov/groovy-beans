package ru.storage

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver

 /**
 * User: dima
 * Date: 19/3/11
 */
class Storage {
  private static final String STORAGE = ".storage"
  static def xStream = new XStream(new DomDriver())

  static def load(String id, Closure closure) {
    def result = load(id)
    if (result == null) {
      result = closure.call()
      if (result != null) save(id, result)
    }
    result
  }

  static def load(String id) {
    def xml = readXml(id)
    if (xml == null) return null

    xStream.fromXML(xml)
  }

  static def save(String id, def object) {
    def xml = xStream.toXML(object)
    saveXml(id, xml)
  }

  static def saveXml(String id, String xml) {
    def storageFolder = new File(STORAGE)
    if (!storageFolder.exists()) storageFolder.mkdir()

    def file = new File("${STORAGE}/${id}.xml")
    file.createNewFile()
    file.write(xml)
  }

  private static String readXml(String id) {
    def file = new File("${STORAGE}/${id}.xml")
    if (!file.exists()) return null

    new String(file.readBytes())
  }
}
