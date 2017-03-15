package com.peterdkahn.examples.workspace

import io.vertx.core.json.JsonObject

import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributes

/**
 * Created by pkahn on 3/14/17.
 *
 * Data object for a file
 * permissions
 * size
 * mod date
 * user
 * group
 * name
 *
 */
class FileInfo {
  final static String FILE_TYPE_FILE = "file"
  final static String FILE_TYPE_DIRECTORY = "directory"
  final static String FILE_TYPE_SYMLINK = "symlink"

  final static String JSON_FIELD_NAME = "name"
  final static String JSON_FIELD_CLOC = "cloc"
  final static String JSON_FIELD_USER = "user"
  final static String JSON_FIELD_GROUP = "group"
  final static String JSON_FIELD_PERMISSON = "PERMISSON"
  final static String JSON_FIELD_FILETYPE = "TYPE"
  final static String JSON_FIELD_SIZE = "size"

  private final File target
  private final PosixFileAttributes posixAttrib
  private ClocInfo clocInfo

  FileInfo(File target) {
    this.target = target

    posixAttrib =
      Files.readAttributes(target.toPath(), PosixFileAttributes.class)

    clocInfo = new ClocInfo(target)
  }

  String getFileType() {
    if (target.absolutePath == target.canonicalPath) {
      if (target.isFile() ) {
        return FILE_TYPE_FILE
      } else {
        return FILE_TYPE_DIRECTORY
      }
    } else {
      return FILE_TYPE_SYMLINK
    }
  }

  JsonObject toJson() {
    JsonObject json = new JsonObject()
    json.put(JSON_FIELD_NAME, name)
    json.put(JSON_FIELD_USER, userOwner)
    json.put(JSON_FIELD_GROUP, groupOwner)
    json.put(JSON_FIELD_PERMISSON, permisionsString)
    json.put(JSON_FIELD_SIZE, size)
    json.put(JSON_FIELD_FILETYPE, fileType)
    json.put(JSON_FIELD_CLOC, clocInfo.toJson())
    return json
  }

  ClocInfo getClocInfo() {
    return clocInfo
  }

  Date getModification() {
    return new Date(posixAttrib.lastModifiedTime())
  }

  String getName() {
    return target.name
  }

  String getUserOwner() {
    return posixAttrib.owner().name
  }

  String getGroupOwner() {
    return posixAttrib.group().name
  }

  String getPermisionsString() {
    return posixAttrib.permissions().toString()
  }

  long getSize() {
    return posixAttrib.size()
  }

}
