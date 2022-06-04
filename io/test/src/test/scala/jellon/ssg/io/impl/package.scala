package jellon.ssg.io

import java.io.File

package object impl {
  val expectedContents: String = "foobar"

  val validResourcePath: String = "jellon/ssg/io/impl/testFile.txt"

  val invalidResourcePath: String = "doesNotExist.txt"

  val basePath: String = {
    val path = "src/test/resources"
    if (new File(new File("io"), "test").isDirectory) s"io/test/$path"
    else path
  }

  val baseDir: File = new File(basePath)

  val validFile: File = new File(s"$basePath/$validResourcePath")

  val invalidFile: File = new File(s"$basePath/$invalidResourcePath")
}
