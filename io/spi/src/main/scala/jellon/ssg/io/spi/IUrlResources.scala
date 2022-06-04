package jellon.ssg.io.spi

import java.io.IOException
import java.net.URL

/** All sorts of methods to get an instance or Option of URL without a hint */
trait IUrlResources {
  def optURL(resource: String): Option[URL]

  @throws[IOException]
  def openURL(resource: String): URL = optURL(resource) match {
    case Some(url) =>
      url
    case None =>
      throw new IOException(s"Unable to find url '$resource'")
  }
}
