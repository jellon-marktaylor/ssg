package jellon.ssg.io.impl

import jellon.ssg.io.impl.FileHelper.describePath
import jellon.ssg.io.spi.IUrlResources

import java.io.{File, IOException}
import java.net.URL
import scala.util.Try

object UrlResources {
  def optURL(baseDir: File, resource: String): Option[URL] = {
    optFileURL(baseDir, resource)
      .orElse(optSystemClassLoaderResource(resource))
      .orElse(optSystemResource(resource))
  }

  def optFileURL(baseDir: File, resource: String): Option[URL] =
    optFileURL(new File(baseDir, resource))
      .orElse(
        Try(
          new URL(resource)
        ).toOption
      )

  def optFileURL(file: File): Option[URL] =
    if (file.isFile && file.canRead)
      Try(
        file.toURI.toURL
      ).toOption
    else
      Option.empty

  def optSystemClassLoaderResource(resource: String): Option[URL] =
    Option(ClassLoader.getSystemClassLoader.getResource(resource))

  def optSystemResource(resource: String): Option[URL] =
    Option(ClassLoader.getSystemResource(resource))
}

class UrlResources(baseDir: File) extends IUrlResources {
  override def optURL(resource: String): Option[URL] =
    UrlResources.optFileURL(baseDir, resource)

  override def openURL(resource: String): URL =
    try {
      super.openURL(resource)
    } catch {
      case cause: IOException =>
        throw new IOException(s"${cause.getLocalizedMessage} in dir ${describePath(baseDir)}", cause)
    }
}
