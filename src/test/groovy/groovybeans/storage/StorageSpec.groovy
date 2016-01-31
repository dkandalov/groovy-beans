package groovybeans.storage

import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.fail
import static groovybeans.beans.Bean.bean


class StorageSpec {
  private static String TEST_ID = StorageSpec.class.simpleName + "_test_object"

  @Test
  public void shouldAssertThatValueIsTheSameAsLastSavedValue() {
    try {
      Storage.assertSameAsLast(TEST_ID) { 123 }
    } catch (AssertionError e) {
      fail()
    }

    try {
      def i = 0
      Storage.assertSameAsLast(TEST_ID) { i++ }
      fail()
    } catch (AssertionError ignore) {}
  }

  @Test void shouldSaveAndLoadObject() {
    def testObject = "test object"

    Storage.save(TEST_ID, testObject)
    def loadedObject = Storage.load(TEST_ID)

    assert testObject == loadedObject
  }

  @Test void shouldSaveAndLoadSingletonCollection() {
    def testObject = ["test object"]

    Storage.save(TEST_ID, testObject)
    def loadedObject = Storage.load(TEST_ID)

    assert testObject == loadedObject
  }

  @Test void shouldSaveAndLoadCollectionOfObjects() {
    def testObject = (0..10).collect { it }

    Storage.save(TEST_ID, testObject)
    def loadedObject = Storage.load(TEST_ID)

    assert testObject == loadedObject
  }

  @Test void shouldCacheCollectionOfObjects_AsCsv() {
    def testObject = Storage.cachedCsv(TEST_ID){ (0..10).collect{ bean([value: it]) } }
    Storage.cachedCsv(TEST_ID){ fail("shouldn't be called") }

    assert testObject == (0..10).collect{ bean([value: it]) }
  }

  @Before @After void deleteTestObject() {
    Storage.delete(TEST_ID)
    Storage.deleteCsv(TEST_ID)
  }
}
