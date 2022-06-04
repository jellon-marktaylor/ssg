package jellon.ssg.io.impl

import java.io.File

object FileHelper {
  // oddly enough, a NoSuchMethodError was being thrown when I put this in a package object.
  // Not going to spend time figuring out why when this work-around solves it cleanly.
  def describePath(file: File): String = {
    val absPath = file.getAbsolutePath
    try {
      s"$absPath <= (${file.getCanonicalPath})"
    } catch {
      case _: Exception =>
        absPath
    }
  }
}
