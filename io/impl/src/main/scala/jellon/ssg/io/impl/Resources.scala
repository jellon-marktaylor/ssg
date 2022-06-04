package jellon.ssg.io.impl

import jellon.ssg.io.spi._
import org.springframework.stereotype.Component

import java.io.{InputStream, OutputStream}
import java.net.URL
import javax.inject.Inject

@Component
class Resources(urls: IUrlResources, inputResources: IInputStreamResources, outputResources: IOutputStreamResources, handlers: IHintHandlers) extends IResources {
  @Inject
  def this(handlers: IHintHandlers, urls: IUrlResources, inputResources: IInputStreamResources, outputResources: IOutputStreamResources) =
    this(urls, inputResources, outputResources, handlers)

  ////
  // IUrlResources
  ////

  override def openURL(resource: String): URL =
    urls.openURL(resource)

  override def optURL(resource: String): Option[URL] =
    urls.optURL(resource)

  override def optURL(resource: String, hint: String): Option[URL] =
    handlers.optURL(resource, hint)
      .orElse(urls.optURL(resource))

  ////
  // IInputStreamResources
  ////

  override def openInputStream(resource: String): InputStream =
    inputResources.openInputStream(resource)

  override def optInputStream(resource: String): Option[InputStream] =
    inputResources.optInputStream(resource)

  override def optInputStream(resource: String, hint: String): Option[InputStream] =
    handlers.optInputStream(resource, hint)
      .orElse(inputResources.optInputStream(resource))

  ////
  // IOutputStreamResources
  ////

  override def openOutputStream(resource: String): OutputStream =
    outputResources.openOutputStream(resource)

  override def optOutputStream(resource: String): Option[OutputStream] =
    outputResources.optOutputStream(resource)

  override def optOutputStream(resource: String, hint: String): Option[OutputStream] =
    handlers.optOutputStream(resource, hint)
      .orElse(outputResources.optOutputStream(resource))
}
