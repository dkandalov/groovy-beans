package ru.storage

import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.fail

/**
 * User: DKandalov
 */
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

  @Before @After void deleteTestObject() {
    Storage.delete(TEST_ID)
  }
}
