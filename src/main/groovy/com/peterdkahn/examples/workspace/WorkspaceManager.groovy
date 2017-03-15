package com.peterdkahn.examples.workspace

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject

import javax.activation.MimetypesFileTypeMap
import java.nio.file.Files


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

  /**
   * Obtain content of file
   * @param path within workspace
   * @return contents, text or od output for binary
   */
  String getContent(String path) {
    File target = getWorkspaceFile(path)
    if (target.isDirectory()) {
      def names = target.listFiles().collect() { File f -> f.name }
      JsonObject json = new JsonObject()
      json.put("files", names)
      return json.toString()
    } else {
      if (! isBinary(target)) {
        return target.text
      } else {
        return OctalDumper.dump(target)
      }
    }

  }

  boolean isDirectory(String path) {
    File target = getWorkspaceFile(path)
    return target.exists() && target.isDirectory()
  }

  boolean isBinary(File target) {
    if ('text' == Files.probeContentType(target.toPath()).split(/\//)[0]) {
      return false
    } else {
      return true
    }
  }

  /**
   * Generate json output for directory or file
   * @param path within workspace
   * @return file listing for directory, FileInfo for file
   */
  JsonObject pathToJson(String path) {
    File target = getWorkspaceFile(path)
    if (! target.exists()) {
      throw new IOException("Cannot locate ${path}")
    }

    if (target.isDirectory()) {
      def list = []
      target.listFiles().each { File f ->
        def map = [:]
        map.put('name', f.name)
        if (f.isFile()) {
          map.put('type', 'file')
        } else {
          map.put('type', 'directory')
        }
        list.add(map)
      }

      JsonObject json = new JsonObject()
      json.put('files', list)
      return json
    } else {
      FileInfo info = new FileInfo(target)
      return info.toJson()
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
