package com.peterdkahn.examples.vertx.app

import io.vertx.core.Vertx
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner.class)
class ApplicationTest extends TestBase{
  private File testDir
  private Vertx vertx

  @Before
  void setUp(TestContext tc) {
    testDir = createEmptyTestDir()
    vertx = Vertx.vertx()
    vertx.deployVerticle(Application.class.getName(), tc.asyncAssertSuccess())
  }

  @After
  void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess())
  }

  @Test
  void happyPathDepthUriTestFile(TestContext tc) {
    // Given simple workspace with sub1/sub2/bar.txt
    createWorkspace(["sub1/sub2/bar.txt"])
    setAppWorkspace(tc, testDir)

    // When  /api/lib/sub1/sub2
    vertx.createHttpClient().getNow(8080,"localhost", "${Application.BASEURI}/sub1/sub2/bar.txt") { response ->
      tc.assertEquals(response.statusCode(), 200)
      response.bodyHandler() { body ->
        // Then expect bar.txt as file

        tc.assertTrue(body.length() > 0)
        println "body: ${body}"
        tc.assertTrue("Expected to fine bar.txt file", body.toString().contains("bar.txt"))
        async.complete()

      }
    }
  }

  @Test
  void happyPathDepthUriTestDir(TestContext tc) {
    // Given simple workspace with sub1/sub2/bar.txt
    createWorkspace(["sub1/sub2/bar.txt"])
    setAppWorkspace(tc, testDir)

    // When  /api/lib/sub1/sub2
    vertx.createHttpClient().getNow(8080,"localhost", "${Application.BASEURI}/sub1/sub2") { response ->
      tc.assertEquals(response.statusCode(), 200)
      response.bodyHandler() { body ->
        // Then expect bar.txt as file

        tc.assertTrue(body.length() > 0)
        println "body: ${body}"
        tc.assertTrue("Expected to fine bar.txt file", body.toString().contains("bar.txt"))
        async.complete()

      }
    }
  }

  // Set new workspace location for tests
  private void setAppWorkspace(TestContext tc, File root) {
    Async async = tc.async()
    vertx.eventBus().send(Application.EVENT_SET_WORKSPACE, root.absolutePath, { reply ->
      println "TADA ${reply}"
    })
    async.complete()


  }

  // Create workspace
  private void createWorkspace(List<String> paths) {
    paths.each{ path ->
      File target = new File(testDir, path)
      target.parentFile.mkdirs()
      target << "${path} ${now}"
    }
  }

  @Test
  void testThatTheServerIsStarted(TestContext tc) {
    Async async = tc.async()

    vertx.createHttpClient().getNow(8080,"localhost", Application.BASEURI) { response ->
      tc.assertEquals(response.statusCode(), 200)
      response.bodyHandler() { body ->
        tc.assertTrue(body.length() > 0)
        println "body: ${body}"
        async.complete()

      }
    }
  }
}
