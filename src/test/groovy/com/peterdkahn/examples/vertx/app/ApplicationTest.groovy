package com.peterdkahn.examples.vertx.app

import com.peterdkahn.examples.workspace.FileInfo
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner.class)
class ApplicationTest extends TestBase{
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
    // Given simple workspace with sub1/sub2/foo.c
    def pathParent = "sub1/sub2"
    createCFile(new File(testDir,pathParent))

    setAppWorkspace(tc, testDir)

    // When  /api/lib/sub1/sub2
    Async async = tc.async()
    vertx.createHttpClient().getNow(8080,"localhost", "${Application.BASEURI}/sub1/sub2/foo.c") { response ->
      async.complete()
      tc.assertEquals(response.statusCode(), 200)
      response.bodyHandler() { body ->
        log.debug("body: ${body}")
        def receivedInfo = new JsonObject(body.toString()).getMap()
        tc.assertEquals("foo.c", receivedInfo[FileInfo.JSON_FIELD_NAME])
      }
    }
  }

  @Test
  void happyPathDepthUriTestDir(TestContext tc) {
    // Given simple workspace with sub1/sub2/bar.txt
    createWorkspace(["sub1/sub2/bar.txt"])
    setAppWorkspace(tc, testDir)

    // When  /api/lib/sub1/sub2
    Async async = tc.async()

    vertx.createHttpClient().getNow(8080,"localhost", "${Application.BASEURI}/sub1/sub2") { response ->
      async.complete()
      tc.assertEquals(response.statusCode(), 200)
      response.bodyHandler() { body ->
        // Then expect bar.txt as file

        tc.assertTrue(body.length() > 0)
        log.debug( "body: ${body}")
        tc.assertTrue(body.toString().contains("bar.txt"))
      }
    }
  }

  // Set new workspace location for tests
  private void setAppWorkspace(TestContext tc, File root) {
    Async async = tc.async()
    vertx.eventBus().send(Application.EVENT_SET_WORKSPACE, root.absolutePath, { reply ->
      log.debug("Workspace Set: ${reply}")
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
        log.debug("body: ${body}")
        async.complete()

      }
    }
  }
}
