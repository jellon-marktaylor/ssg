package jellon.ssg.io.impl

import jellon.ssg.io.spi.IUrlResources

import java.net.URL

/**
 * Looks for resources in the current Thread's class loader, the system class loader, or the system resources.
 */
object ClassLoaderUrlResources extends IUrlResources {
  def apply(cl: ClassLoader): IUrlResources =
    new IUrlResources() {
      override def optURL(resource: String): Option[URL] =
        ClassLoaderUrlResources.optURL(cl, resource)
          .orElse(ClassLoaderUrlResources.optURL(resource))
    }

  def apply(prefix: String): IUrlResources =
    new IUrlResources() {
      override def optURL(resource: String): Option[URL] = {
        val prefixed =
          if (prefix.isEmpty) resource
          else s"$prefix/$resource"

        ClassLoaderUrlResources.optURL(prefixed)
      }
    }

  def apply(cl: ClassLoader, prefix: String): IUrlResources =
    new IUrlResources() {
      override def optURL(resource: String): Option[URL] = {
        val prefixed =
          if (prefix.isEmpty) resource
          else s"$prefix/$resource"

        ClassLoaderUrlResources.optURL(cl, prefixed)
          .orElse(ClassLoaderUrlResources.optURL(prefixed))
      }
    }

  override def optURL(resource: String): Option[URL] =
    optURL(Thread.currentThread().getContextClassLoader, resource)
      .orElse(optURL(ClassLoader.getSystemClassLoader, resource))
      .orElse(optSystemResource(resource))

  def optURL(cl: ClassLoader, resource: String): Option[URL] =
    Option(cl).map(_.getResource(resource))

  def optSystemResource(resource: String): Option[URL] = {
    Option(ClassLoader.getSystemResource(resource))
  }
}
