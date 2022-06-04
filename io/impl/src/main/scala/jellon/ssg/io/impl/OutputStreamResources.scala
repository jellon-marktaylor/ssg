package jellon.ssg.io.impl

import jellon.ssg.io.impl.FileHelper.describePath
import jellon.ssg.io.spi.IOutputStreamResources

import java.io.{File, FileOutputStream, IOException, OutputStream}

object OutputStreamResources {
  def optFileOutputStream(baseDir: File, resource: String): Option[OutputStream] =
    optFileOutputStream(new File(baseDir, resource))

  def optFileOutputStream(file: File): Option[OutputStream] = {
    touch(file)
    if (file.canRead && file.isFile)
      Some(new FileOutputStream(file))
    else
      Option.empty
  }

  private def touch(file: File): Unit = {
    if (!file.exists) {
      try {
        assertWritableDirectory(file.getParentFile)
      } catch {
        case io: IOException =>
          throw new IOException(s"when trying to create: ${describePath(file)}", io)
      }

      if (!file.createNewFile) {
        throw new IOException(s"Unable to create file: ${describePath(file)}")
      }
    } else if (!file.canWrite) {
      throw new IOException(s"Unable to write to file: ${describePath(file)}")
    }
  }

  private def assertWritableDirectory(dir: File): Unit =
    if (dir.isDirectory) {
      if (!dir.canWrite) {
        throw new IOException(s"Unable to write to dir: ${describePath(dir)}")
      }
    } else if (!dir.mkdirs())
      throw new IOException(s"Unable to create dir: ${describePath(dir)}")
}

class OutputStreamResources(baseDir: File) extends IOutputStreamResources {
  OutputStreamResources.assertWritableDirectory(baseDir)

  override def optOutputStream(resource: String): Option[OutputStream] =
    OutputStreamResources.optFileOutputStream(baseDir, resource)

  override def openOutputStream(resource: String): OutputStream =
    try {
      super.openOutputStream(resource)
    } catch {
      case cause: IOException =>
        throw new IOException(s"${cause.getLocalizedMessage} in dir ${describePath(baseDir)}", cause)
    }
}
