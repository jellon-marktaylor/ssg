package jellon.ssg.io.impl

import jellon.ssg.io.impl.FileHelper.describePath
import jellon.ssg.io.spi.IInputStreamResources

import java.io.{File, FileInputStream, FileNotFoundException, InputStream}

object InputStreamResources {
  def optInputStream(baseDir: File, resource: String): Option[InputStream] =
    optFileInputStream(baseDir, resource)
      .orElse(ClassLoaderInputStreamResources.optInputStream(resource))

  def optFileInputStream(baseDir: File, resource: String): Option[InputStream] =
    optFileInputStream(new File(baseDir, resource))

  def optFileInputStream(file: File): Option[InputStream] =
    if (file.isFile && file.canRead) Option(new FileInputStream(file))
    else Option.empty
}

class InputStreamResources(baseDir: File) extends IInputStreamResources {
  override def optInputStream(resource: String): Option[InputStream] =
    InputStreamResources.optInputStream(baseDir, resource)

  override def openInputStream(resource: String): InputStream =
    InputStreamResources.optFileInputStream(baseDir, resource) match {
      case Some(value) => value
      case None => throw new FileNotFoundException(s"${describePath(baseDir)}/$resource")
    }
}
