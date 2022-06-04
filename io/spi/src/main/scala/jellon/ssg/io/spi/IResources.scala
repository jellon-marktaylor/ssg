package jellon.ssg.io.spi

import java.io.{IOException, InputStream, OutputStream}
import java.net.URL
import scala.reflect.ClassTag

object IResources {
  def relativeResource(path: String, resource: String): String =
    s"$path$resource"

  def relativeResource(clz: Class[_], resource: String): String =
    relativeResource(s"${clz.getPackage.getName.replace('.', '/')}/", resource)

  def relativeResourceOf[A: ClassTag](resource: String): String =
    relativeResource(implicitly[ClassTag[A]].runtimeClass, resource)

  def relativeResource(self: AnyRef, resource: String): String = self match {
    case path: String => relativeResource(path, resource)
    case _ => relativeResource(self.getClass, resource)
  }
}

/** All sorts of methods to get an (instance or Option) of (URL, InputStream, or OutputStream) (with or without) a hint */
trait IResources extends IUrlResources with IInputStreamResources with IOutputStreamResources {
  ////
  // URL
  ////

  def optURL(resource: String, hint: String): Option[URL]

  @throws[IOException]
  def openURL(resource: String, hint: String): URL =
    if (hint == null || hint.trim.isEmpty) {
      openURL(resource)
    } else optURL(resource, hint) match {
      case Some(url) =>
        url
      case None =>
        try {
          openURL(resource)
        } catch {
          case io: IOException =>
            throw new IOException(s"Unable to find url '$resource' using '$hint'", io)
        }
    }

  ////
  // InputStream
  ////

  def optInputStream(resource: String, hint: String): Option[InputStream]

  @throws[IOException]
  def openInputStream(resource: String, hint: String): InputStream =
    if (hint == null || hint.trim.isEmpty) {
      openInputStream(resource)
    } else optInputStream(resource, hint) match {
      case Some(is) =>
        is
      case None =>
        try {
          openInputStream(resource)
        } catch {
          case io: IOException =>
            throw new IOException(s"Unable to find input stream '$resource' using '$hint'", io)
        }
    }

  ////
  // OutputStream
  ////

  def optOutputStream(resource: String, hint: String): Option[OutputStream]

  @throws[IOException]
  def openOutputStream(resource: String, hint: String): OutputStream =
    if (hint == null || hint.trim.isEmpty) {
      openOutputStream(resource)
    } else optOutputStream(resource, hint) match {
      case Some(is) =>
        is
      case None =>
        try {
          openOutputStream(resource)
        } catch {
          case io: IOException =>
            throw new IOException(s"Unable to find output stream '$resource' using '$hint'", io)
        }
    }
}
