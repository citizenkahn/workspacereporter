package com.peterdkahn.examples.workspace

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import org.yaml.snakeyaml.Yaml

/**
 * Code line count info object -- http://cloc.sourceforge.net/
 * Created by pkahn on 3/14/17.
 */
class ClocInfo {
  final static String JSON_FIELD_BLANK_LINES="blank"
  final static String JSON_FIELD_CODE_LINES="code"
  final static String JSON_FIELD_COMMENT_LINES="comment"
  final static String JSON_FIELD_TOTAL_LINES="total"

  final static String CLOCEXE ="/usr/bin/cloc"
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

  JsonObject toJson() {
    JsonObject json = new JsonObject()
    json.put(JSON_FIELD_BLANK_LINES, blankLines)
    json.put(JSON_FIELD_CODE_LINES, codeLines)
    json.put(JSON_FIELD_COMMENT_LINES, commentLines)
    json.put(JSON_FIELD_TOTAL_LINES, totalLines)
    return json
  }
  /**
   * Read cloc info
   */
  def loadClocInfo() {
    StringBuffer output = new StringBuffer()
    StringBuffer error = new StringBuffer()
    def proc = null

    try {
      proc = "${CLOCEXE} --quiet --yaml ${target.absolutePath}".execute()

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
