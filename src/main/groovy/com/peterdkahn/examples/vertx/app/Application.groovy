package com.peterdkahn.examples.vertx.app

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.ext.web.Router


/**
 * Created by pkahn on 3/14/17.
 */
class Application extends AbstractVerticle {
    private List<String> names = ["a", "b"]
    @Override
    void start() {
      vertx.createHttpServer()
        .requestHandler() { req -> req.response().end("Hello Vert.x!") }
        .listen(8080);

        Router router = Router.router(vertx)

        router.get("/").handler() {rc ->
            rc.response().end("Welcome")
        }

        router.get("/api/linux/**").handler() { rc->
            rc.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(names))
        }
    }

  @Override
  void stop() {
    println "shutdown"
  }
}
