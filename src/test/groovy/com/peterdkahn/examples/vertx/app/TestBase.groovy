package com.peterdkahn.examples.vertx.app

import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.Logger
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.junit.rules.TestName

/**
 *
 * Base class for unit tests
 * Created by pkahn on 3/14/17.
 */
class TestBase {
  Date now = new Date()
  final Logger log = LoggerFactory.getLogger(this.class.name)
  protected File testDir

  @Rule
  public TestName name = new TestName()

  @Rule
  public ExpectedException expectedException = ExpectedException.none()

  protected File createEmptyTestDir(testName = "${getClass().name}-${name.getMethodName()}") {
    File testDir = new File("build/testdata/${testName}")
    if (testDir.exists())
    {
      testDir.deleteDir()
    }
    testDir.mkdirs()
    return testDir
  }

  /**
   * Creates a Simple C File
   * @param name
   * @return File object
   */
  protected File createCFile(File parent = testDir, name = "foo.c") {
    File target = new File(parent, name)
    if (target.exists()) { target.delete() }
    target.parentFile.mkdirs()
    target << """//A C File
#include <stdio.h>
int i = 0;

// a loop
for (i = 1; i < 100; i++) {
  print i;
}
"""
  }

  protected final int CFILE_TOTAL=8
  protected final int CFILE_COMMENT=2
  protected final int CFILE_CODE=5
  protected final int CFILE_BLANK=1
}
