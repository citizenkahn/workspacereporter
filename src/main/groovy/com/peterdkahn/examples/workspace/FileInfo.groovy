package com.peterdkahn.examples.workspace

import java.nio.file.Files
import java.nio.file.attribute.FileTime
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
  private final File target
  private final PosixFileAttributes posixAttrib
  private ClocInfo clocInfo

  FileInfo(File target) {
    this.target = target

    posixAttrib =
      Files.readAttributes(target.toPath(), PosixFileAttributes.class)

    clocInfo = new ClocInfo(target)
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

  String GetPermisionsString() {
    return posixAttrib.permissions().toString()
  }

  long getSize() {
    return posixAttrib.size()
  }

  // CLOC Info
  String getLanguage() {
    return "langauges/langauge name"
  }

  int getBlankLines() {
    return 0
  }
  int getCommentLines() {
    return 0
  }
  int getCodeLines() {
    return 0
  }

  int getTotalLines() {
    return 0
  }
}
