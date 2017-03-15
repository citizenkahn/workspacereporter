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
  private File testDir
  private File cFile
  private File makeFile
  @Before
  void setUp() {
    testDir = createEmptyTestDir()
    cFile = createCFile()
  }

  private File createCFile(name = "foo.c") {
    File target = new File(testDir, name)
    if (target.exists()) { target.delete() }
    target << """//A C File
#include <stdio.h>
int i = 0;

// a loop
for (i = 1; i < 100; i++) {
  print i;
}
"""
  }

  @Test
  void cFileHappyPath() {
    // Given a simple C file

    // When load clock info
    ClocInfo info = new ClocInfo(cFile)

    // Then expect reported quantities
    assertEquals("Total", 8, info.getTotalLines())
    assertEquals("Comments", 2, info.getCommentLines())
    assertEquals("Code", 5, info.getCodeLines())
    assertEquals("Blank", 1, info.getBlankLines())
  }

  @Test
  void ClocFailureMissingFile() {
    // Given a non existent file
    File target = new File(testDir, "doesNotExist.txt")

    // Expect Failure
    expectedException.expect(IOException.class)

    // When clock info loaded
    ClocInfo info = new ClocInfo(target)
  }
}
