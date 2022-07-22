package jellon.ssg.io.spi

import java.io.{IOException, InputStream, OutputStream}
import java.net.URL
import scala.reflect.ClassTag

object IResources {
  /**
   * scala has a limitation such that you can't have both an "object" declaration and a resource path that mirrors the
   * package and class names. The work-around is to have a directory that has the package as it's name, including the
   * periods '.'. This is the same effect as in most IDEs where you can "flatten" a directory structure.
   *
   * @return "packageName"/"className". Eg (String.class => "java.lang/String"
   */
  def relativeResource(clz: Class[_]): String = {
    val packageName = clz.getPackage.getName
    val className = clz.getSimpleName
    s"$packageName/$className"
  }

  def relativeResource(self: AnyRef): String =
    relativeResource(self.getClass)

  def relativeResourceOf[A: ClassTag]: String =
    relativeResource(implicitly[ClassTag[A]].runtimeClass)

  /**
   * scala has a limitation such that you can't have both an "object" declaration and a resource path that mirrors the
   * package and class names. The work-around is to have a directory that has the package as it's name, including the
   * periods '.'. This is the same effect as in most IDEs where you can "flatten" a directory structure.
   *
   * @return "packageName"/"className". Eg ((String.class, "subdir") => "java.lang/String/subdir"
   */
  def relativeResource(clz: Class[_], resource: String): String =
    s"${relativeResource(clz)}/$resource}"

  def relativeResource(self: AnyRef, resource: String): String =
    relativeResource(self.getClass, resource)

  def relativeResourceOf[A: ClassTag](resource: String): String =
    relativeResource(implicitly[ClassTag[A]].runtimeClass, resource)
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
