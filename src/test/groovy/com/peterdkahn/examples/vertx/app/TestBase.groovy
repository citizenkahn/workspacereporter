package com.peterdkahn.examples.vertx.app

import org.junit.Rule
import org.junit.rules.ExpectedException
import org.junit.rules.TestName

/**
 * Created by pkahn on 3/14/17.
 */
class TestBase {
  Date now = new Date()

  @Rule
  public TestName name = new TestName();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  protected File createEmptyTestDir(def testName = "${getClass().name}-${name.getMethodName()}") {
    File testDir = new File("build/testdata/${testName}")
    if (testDir.exists())
    {
      testDir.deleteDir()
    }
    testDir.getParentFile().mkdirs()
    return testDir
  }
}
