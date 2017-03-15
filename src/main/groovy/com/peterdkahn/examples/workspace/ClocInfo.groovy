package com.peterdkahn.examples.workspace

import org.yaml.snakeyaml.Yaml

/**
 * Code line count info object -- http://cloc.sourceforge.net/
 * Created by pkahn on 3/14/17.
 */
class ClocInfo {
  final static String clocExe="/usr/bin/cloc"
  private final File target
  private Map clocData
  ClocInfo(File target) {
      this.target = target

    clocData = loadClocInfo()
  }

  int getBlankLines() { return (int) clocData['SUM']["blank"] }
  int getCodeLines() { return (int) clocData['SUM']["code"] }
  int getCommentLines() { return (int) clocData['SUM']["comment"] }
  int getTotalLines() { return getBlankLines() + getCodeLines() + getCommentLines() }

  /**
   * Read cloc info
   */
  def loadClocInfo() {
    StringBuffer output = new StringBuffer()
    StringBuffer error = new StringBuffer()
    def proc = null

    try {
      proc = "${clocExe} --quiet --yaml ${target.absolutePath}".execute()

      proc.consumeProcessOutput(output, error)
      proc.waitFor()

    } finally {
      // Cannot trust existValue as it's always 0 regardless of error

      proc.closeStreams()
      proc.destroy()

      if (error.size() > 0) {
        throw new IOException("Failed to read cloc data from ${target.absolutePath} because ${error.toString()}")
      }
    }

    Yaml yaml = new Yaml()
    Map map = (Map) yaml.load(output.toString())
    return map
  }

}
