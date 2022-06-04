package jellon.ssg.io.impl

import jellon.ssg.io.spi.IInputStreamResources

import java.io.InputStream

/**
 * Looks for resources in the current Thread's class loader, the system class loader, or the system resources.
 */
object ClassLoaderInputStreamResources extends IInputStreamResources {
  def apply(cl: ClassLoader): IInputStreamResources =
    new IInputStreamResources() {
      override def optInputStream(resource: String): Option[InputStream] =
        ClassLoaderInputStreamResources.optInputStream(cl, resource)
          .orElse(ClassLoaderInputStreamResources.optInputStream(resource))
    }

  def apply(prefix: String): IInputStreamResources =
    new IInputStreamResources() {
      override def optInputStream(resource: String): Option[InputStream] = {
        val prefixed =
          if (prefix.isEmpty) resource
          else s"$prefix/$resource"

        ClassLoaderInputStreamResources.optInputStream(prefixed)
      }
    }

  def apply(cl: ClassLoader, prefix: String): IInputStreamResources =
    new IInputStreamResources() {
      override def optInputStream(resource: String): Option[InputStream] = {
        val prefixed =
          if (prefix.isEmpty) resource
          else s"$prefix/$resource"

        ClassLoaderInputStreamResources.optInputStream(cl, prefixed)
          .orElse(ClassLoaderInputStreamResources.optInputStream(prefixed))
      }
    }

  override def optInputStream(resource: String): Option[InputStream] =
    optInputStream(Thread.currentThread().getContextClassLoader, resource)
      .orElse(optInputStream(ClassLoader.getSystemClassLoader, resource))
      .orElse(optSystemResourceAsStream(resource))

  def optInputStream(cl: ClassLoader, resource: String): Option[InputStream] =
    Option(cl).map(_.getResourceAsStream(resource))

  def optSystemResourceAsStream(resource: String): Option[InputStream] = {
    Option(ClassLoader.getSystemResourceAsStream(resource))
  }
}
