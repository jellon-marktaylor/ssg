package jellon.ssg.io.spi

import java.io.{IOException, OutputStream}

/** All sorts of methods to get an instance or Option of OutputStream without a hint */
trait IOutputStreamResources {
  def optOutputStream(resource: String): Option[OutputStream]

  @throws[IOException]
  def openOutputStream(resource: String): OutputStream = optOutputStream(resource) match {
    case Some(is) =>
      is
    case None =>
      throw new IOException(s"Unable to find output stream '$resource'")
  }
}
