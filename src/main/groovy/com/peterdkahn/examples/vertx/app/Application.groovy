package com.peterdkahn.examples.vertx.app

import com.peterdkahn.examples.workspace.WorkspaceManager
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by pkahn on 3/14/17.
 * Web application using vertx for rest api
 */
class Application extends AbstractVerticle {
    private List<String> names = ["a", "b"]
    private File workspace = new File("/home/pkahn/code/linux-stable")
    private WorkspaceManager workspaceManager

    private final Pattern workspacePattern = Pattern.compile("/api/linux(/([^?]+))?.*")
    @Override
    void start() {
      // Setup mangager
      workspaceManager = new WorkspaceManager(workspace)

      // Setup Server
      Router router = Router.router(vertx)

      // Set Handler
      router.routeWithRegex(HttpMethod.GET, "/api/linux.*").handler( { context->
        //def uri = context.request().uri().split(/\?/)[0]
        def path = getWorkspacePath(context.request().uri())

        context.response()
          .putHeader("content-type", "application/json")
          .end(workspaceManager.pathToJson(path).toString())
      })

      vertx.createHttpServer()
        .requestHandler(router.&accept)
        .listen(8080)


    }

  def getWorkspacePath(def uri) {
    Matcher m = workspacePattern.matcher(uri)
    if (! m.matches()) {
      throw new IOException("bad file for ${uri}")
    }
    return m.group(2)
  }
  @Override
  void stop() {
    println "shutdown"
  }
}
