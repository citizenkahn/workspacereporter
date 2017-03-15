package com.peterdkahn.workspace

import com.peterdkahn.examples.vertx.app.TestBase
import com.peterdkahn.examples.workspace.ClocInfo
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

/**
 * Unit Tests for code line counting tool
 * Created by pkahn on 3/14/17.
 */
class ClocInfoTests extends TestBase {
  private File cFile
  private File makeFile
  @Before
  void setUp() {
    testDir = createEmptyTestDir()
    cFile = createCFile()
  }


  @Test
  void cFileHappyPath() {
    // Given a simple C file

    // When load clock info
    ClocInfo info = new ClocInfo(cFile)

    // Then expect reported quantities
    assertEquals("Total", CFILE_TOTAL, info.getTotalLines())
    assertEquals("Comments", CFILE_COMMENT, info.getCommentLines())
    assertEquals("Code", CFILE_CODE, info.getCodeLines())
    assertEquals("Blank", CFILE_BLANK, info.getBlankLines())
  }

  @Test
  void ClocFailureMissingFile() {
    // Given a non existent file
    File target = new File(testDir, "doesNotExist.txt")

    // Expect Failure
    expectedException.expect(IOException.class)

    // When clock info loaded
    new ClocInfo(target)
  }
}
