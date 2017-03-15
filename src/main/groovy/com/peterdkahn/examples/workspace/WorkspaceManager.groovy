package com.peterdkahn.examples.workspace


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

  List<File> getChildren(String path) {
    File child = workspaceRoot
    if ("/" != path) {
      child = new File(workspaceRoot, path)
    }
    if (child.isDirectory() && child.exists()) {
      return child.listFiles()
    } else {
      throw new IOException("Cannot process ${child.absolutePath} - unknown file type")
    }
  }

}
