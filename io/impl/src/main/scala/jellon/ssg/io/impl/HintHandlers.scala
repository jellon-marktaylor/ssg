package jellon.ssg.io.impl

import jellon.ssg.io.api.IHintHandler
import jellon.ssg.io.spi.IHintHandlers
import org.springframework.stereotype.Component

import java.io.{InputStream, OutputStream}
import java.net.URL
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

/** Provides static access to implemented methods, allowing for easier re-use */
object HintHandlers {
  def optURL(handlers: Seq[IHintHandler], resource: String, hint: String): Option[URL] =
    findFirst(handlers, _.optURL(resource, hint))

  def optInputStream(handlers: Seq[IHintHandler], resource: String, hint: String): Option[InputStream] =
    findFirst(handlers, _.optInputStream(resource, hint))

  def optOutputStream(handlers: Seq[IHintHandler], resource: String, hint: String): Option[OutputStream] =
    findFirst(handlers, _.optOutputStream(resource, hint))

  /** Lazily finds the first [[IHintHandler]] that returns a non-empty result, if any
   *
   * @param handlers custom hint handlers for the application being run
   * @param mapper   call the appropriate method on each HintHandler
   * @tparam A type of Option returned
   * @return the first successful non-empty result
   */

  private[this] def findFirst[A](handlers: Seq[IHintHandler], mapper: IHintHandler => Option[A]): Option[A] = handlers
    .to(LazyList)
    .map(handlers =>
      Try(
        mapper(handlers)
      )
    )
    .filter(
      _.isSuccess
    )
    .map(
      _.get // Try[Option[A]] => Option[A]
    )
    .filter(
      _.isDefined
    )
    .map(
      _.get // Option[A] => A
    )
    .headOption
}

/** Default implementation of [[IHintHandlers]]. The only change needed to customize is to add your hint handler
 * implementation in such a way that this constructor is called with an instance of it.
 *
 * @param handlers to delegate calls to
 */
class HintHandlers(val handlers: Seq[IHintHandler]) extends IHintHandlers {
  @Component
  def this(handlers: java.util.List[IHintHandler]) = this(handlers.asScala.toSeq)

  override def optURL(resource: String, hint: String): Option[URL] =
    HintHandlers.optURL(handlers, resource, hint)

  override def optInputStream(resource: String, hint: String): Option[InputStream] =
    HintHandlers.optInputStream(handlers, resource, hint)

  override def optOutputStream(resource: String, hint: String): Option[OutputStream] =
    HintHandlers.optOutputStream(handlers, resource, hint)
}
