package com.peterdkahn.examples.vertx.app

import com.peterdkahn.examples.workspace.WorkspaceManager
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.Logger
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by pkahn on 3/14/17.
 * Web application using vertx for rest api
 */
class Application extends AbstractVerticle {
  public final static String EVENT_SET_WORKSPACE = "setWorkspace"
  public final static String BASEURI= "/api/linux"

  private List<String> names = ["a", "b"]
  private WorkspaceManager workspaceManager
  private Logger log

  void setWorkspaceRoot(String rootPath) {
    File rootDir = new File(rootPath)
    log.info("Setting workspace root to ${rootDir.absolutePath}")
    workspaceManager = new WorkspaceManager(rootDir)
  }
  private final Pattern workspacePattern = Pattern.compile("/api/linux(/([^?]+))?.*")
  @Override
  void start() {
    log = LoggerFactory.getLogger(this.class.name)

    // Set Default  @TODO switch to property
    setWorkspaceRoot("/home/pkahn/code/linux-stable")



    // Setup Server
    Router router = Router.router(vertx)

    BridgeOptions options = new BridgeOptions().
      addOutboundPermitted(
        new PermittedOptions().
          setAddress(EVENT_SET_WORKSPACE)
      );

    router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options))

    // Set Handler
    router.routeWithRegex(HttpMethod.GET, "${BASEURI}.*").handler( { context->
      //def uri = context.request().uri().split(/\?/)[0]
      def path = getWorkspacePath(context.request().uri())
      def jsonString = workspaceManager.pathToJson(path).toString()
      log.info("Marshalled JSON ${jsonString}")
      context.response()
        .putHeader("content-type", "application/json")
        .end(jsonString)
    })


    HttpServer httpServer = vertx.createHttpServer()
    httpServer.requestHandler(router.&accept).listen(8080)

    // Listen for change to workspace
    vertx.eventBus.consumer(EVENT_SET_WORKSPACE).handler({ Message message ->
      setWorkspaceRoot(message.body().toString())
      message.reply("OK")
    })

  }

  def getWorkspacePath(uri) {
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
