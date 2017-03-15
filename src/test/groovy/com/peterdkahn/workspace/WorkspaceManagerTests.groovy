package com.peterdkahn.workspace

import com.peterdkahn.examples.vertx.app.TestBase
import com.peterdkahn.examples.workspace.FileInfo
import com.peterdkahn.examples.workspace.OctalDumper
import com.peterdkahn.examples.workspace.WorkspaceManager
import org.hamcrest.core.StringContains
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

/**
 * Workspace access tests
 * Created by pkahn on 3/14/17.
 */
class WorkspaceManagerTests extends TestBase {
  private WorkspaceManager manager
  @Before
  void setUp() {
    testDir = createEmptyTestDir()
  }

  @Test
  void detectTextFiles() {
    manager = new WorkspaceManager(testDir)
    def fileMap = [:]
    fileMap.put(createCFile(), false)
    fileMap.put(new File("/dev/null"), true)

    fileMap.each { k, v ->
      assertEquals("Check Type of ${k}", v, manager.isBinary(k))
    }
  }
  @Test
  void octalDumpHappyPath() {
    File cFile =createCFile()
    String expected="""0000000 027457 020101 020103 064506 062554 021412 067151 066143
0000020 062165 020145 071474 062164 067551 064056 005076 067151
0000040 020164 020151 020075 035460 005012 027457 060440 066040
0000060 067557 005160 067546 020162 064450 036440 030440 020073
0000100 020151 020074 030061 035460 064440 025453 020051 005173
0000120 020040 071160 067151 020164 035551 076412 000012
0000135
"""
    String result  = OctalDumper.dump(cFile)
    assertEquals(expected, result)
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
  void  getInfoHappyPath() {
    File childFile = new File(testDir, "childFile.txt")
    childFile << "data"

    FileInfo info = new FileInfo(childFile)
    assertEquals("User", System.getenv("USER"), info.userOwner)

    assertEquals("Size", 4, info.size)

    FileInfo etcHosts = new FileInfo(new File("/etc/hosts"))
    assertEquals("Group", "root", etcHosts.groupOwner)

  }
}
