package com.peterdkahn.examples.workspace

import io.vertx.core.json.JsonObject
import org.yaml.snakeyaml.Yaml

/**
 * Octal Dumper
 * Created by pkahn on 3/17/17.
 */
class OctalDumper {

  final static String EXE ="/usr/bin/od"

  static String dump(File target) {
    StringBuffer output = new StringBuffer()
    StringBuffer error = new StringBuffer()
    def proc = null

    try {
      proc = "${EXE} ${target.absolutePath}".execute()
      proc.consumeProcessOutput(output, error)
      proc.waitFor()

    } finally {
      proc.closeStreams()
      proc.destroy()

      if (error.size() > 0) {
        throw new IOException("Failed to octal dump ${target.absolutePath} because ${error.toString()}")
      }
    }

    return output.toString()
  }

}
