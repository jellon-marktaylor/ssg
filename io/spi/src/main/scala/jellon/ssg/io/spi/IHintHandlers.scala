package jellon.ssg.io.spi

import java.io.{InputStream, OutputStream}
import java.net.URL

/** Wraps all the [[IHintHandler]] implementations into a single implementation. This is especially useful when using
 * DI libraries which make injecting a list of objects more difficult, such as Guice. */
trait IHintHandlers {
  def optURL(resource: String, hint: String): Option[URL]

  def optInputStream(resource: String, hint: String): Option[InputStream]

  def optOutputStream(resource: String, hint: String): Option[OutputStream]
}
