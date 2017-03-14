package com.peterdkahn.examples.vertx.app

import org.hamcrest.core.StringContains
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

import java.io.File

/**
 * Created by pkahn on 3/14/17.
 */
class WorkspaceManagerTests extends TestBase {

  private File testDir
  private WorkspaceManager manager
  @Before
  void setUp() {
    testDir = createEmptyTestDir()
  }

  @Test
  void misingWorkspace() {
    // Given Missing Directory that does not exist
    File missingDirectory = new File(testDir, "missingDirectory")
    // Expect failure
    expectedException.expect(IOException.class)
    expectedException.expectMessage(new StringContains("does not exist"))
    // When configure manager on directory
    manager = new WorkspaceManager(missingDirectory)
  }

  @Test
  void listExpectedChildren() {
    // Given directory with 2 children: file and dir
    File childFile = new File(testDir, "childFile.txt")
    File childDir = new File(testDir, "childdir")
    childDir.mkdirs()
    childFile << now

    manager = new WorkspaceManager(testDir)

    // When obtain list
    def children = manager.getChildren("/")

    // Then expect both listed
    assertEquals(2, children.size())
    [childDir, childFile].each { File expectedFile ->
      def found = children.find() { it.absolutePath.equals(expectedFile.absolutePath) }
      assertNotNull("${expectedFile} not found in ${children}", found)
    }
  }

  @Test
  void  getInfo() {


  }
}
