package jellon.ssg.io.api

import java.io.{InputStream, OutputStream}
import java.net.URL

/** Allows customization for [[jellon.ssg.io.spi.IUrlResources]], [[jellon.ssg.io.spi.IInputStreamResources]],
  * [[jellon.ssg.io.spi.IOutputStreamResources]], or [[jellon.ssg.io.spi.IResources]]
  */
trait IHintHandler {
  def optURL(resource: String, hint: String): Option[URL] = Option.empty

  def optInputStream(resource: String, hint: String): Option[InputStream] = Option.empty

  def optOutputStream(resource: String, hint: String): Option[OutputStream] = Option.empty
}
