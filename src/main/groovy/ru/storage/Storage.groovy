package ru.storage

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.StaxDriver

/**
 * User: dima
 * Date: 19/3/11
 */
class Storage { // TODO document
  private static final String STORAGE = ".storage"
  static def xStream = new XStream(new StaxDriver())

  static def cachedReload(String id, Closure closure) {
    def result = closure.call()
    save(id, result)
    result
  }

  static def cached(String id, Closure closure) {
    def result = load(id)
    if (result == null) {
      result = closure.call()
      if (result != null) save(id, result)
    }
    result
  }

  static def load(String id, String storage = STORAGE) {
    def xml = readXml(id)
    if (xml == null) return null

    xStream.fromXML(xml)
  }

  static def save(String id, Collection collection, String storage = STORAGE) {
    ObjectOutputStream outputStream = xStream.createObjectOutputStream(new FileWriter(fileFor(id, storage)))
    outputStream.withStream { stream ->
      collection.each { stream.writeObject(it) }
    }
  }

  static def save(String id, def object, String storage = STORAGE) {
    def xml = xStream.toXML(object)
    saveXml(id, xml, storage)
  }

  private static def saveXml(String id, String xml, String storage = STORAGE) {
    def storageFolder = new File(storage)
    if (!storageFolder.exists()) storageFolder.mkdir()

    def file = fileFor(id, storage)
    file.createNewFile()
    file.write(xml)
  }

  private static String readXml(String id, String storage = STORAGE) {
    def file = fileFor(id, storage)
    if (!file.exists()) return null

    new String(file.readBytes())
  }

  private static File fileFor(String id, String storage) {
    new File("${storage}/${id}.xml")
  }
}
