package com.peterdkahn.examples.workspace

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject


/**
 * Workspace Information Manager -- reports on file, files, cloc info
 * Created by pkahn on 3/14/17.
 */
class WorkspaceManager {
  private final File workspaceRoot
  WorkspaceManager(File workspaceRoot) {
    this.workspaceRoot = workspaceRoot

    if(workspaceRoot == null ) {
      throw new IOException("Cannot locate workspace root")
    }
    if(! workspaceRoot.exists()) {
      throw new IOException("Workspace (${workspaceRoot.absolutePath}) does not exist")
    }
    if (! workspaceRoot.isDirectory()) {
      throw new IOException("Workspace (${workspaceRoot.absolutePath}) is not a directory")
    }
  }

  JsonObject pathToJson(String path) {
    File target = getWorkspaceFile(path)
    if (! target.exists()) {
      throw new IOException("Cannot locate ${path}")
    }

    if (target.isDirectory()) {
      def names = target.listFiles().collect() { File f -> f.name }
      JsonObject json = new JsonObject()
      json.put("files", names)
      return json
    } else {
      Json.encode(new FileInfo(target))
    }
  }

  private getWorkspaceFile(String path) {
    File target = workspaceRoot
    if (path != null && "/" != path) {
      target = new File(workspaceRoot, path)
    }
    return target
  }

  List<File> getChildren(String path) {
    File target = getWorkspaceFile(path)
    if (target.isDirectory() && target.exists()) {
      return target.listFiles()
    } else {
      throw new IOException("Cannot process ${target.absolutePath} - unknown file type")
    }
  }

}
