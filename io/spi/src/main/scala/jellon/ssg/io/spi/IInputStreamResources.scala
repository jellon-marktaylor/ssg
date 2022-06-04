package jellon.ssg.io.spi

import java.io.{IOException, InputStream}

/** All sorts of methods to get an instance or Option of InputStream without a hint */
trait IInputStreamResources {
  def optInputStream(resource: String): Option[InputStream]

  @throws[IOException]
  def openInputStream(resource: String): InputStream = optInputStream(resource) match {
    case Some(is) =>
      is
    case None =>
      throw new IOException(s"Unable to find input stream '$resource'")
  }
}
