package jellon.ssg.io

/** These traits provide API to get instances or Options of URL, InputStream, and OutputStream with or without a string
  * hint. The core of this package is driven by [[jellon.ssg.io.api.IHintHandler]] and [[jellon.ssg.io.spi.IResources]].
  * In a typical use-case, IResources will be provided along with some standard IHintHandlers. To modify the default
  * behavior, simply provide instances of IHintHandler in such as way that [[jellon.ssg.io.spi.IHintHandlers]] will pick
  * it up in the DI library used by your app.
  */
package object api {
  val emptyString: String = ""
}
