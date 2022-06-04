package jellon.ssg.io

import java.io.{ByteArrayOutputStream, IOException, InputStream, OutputStream}
import java.net.URL
import scala.io.{BufferedSource, Source}

object Contents {
  def ofURL(source: => URL): String = {
    assert(source != null)
    contentsOf(Source.fromURL(source))
  }

  def ofURL(source: => Option[URL]): Option[String] = {
    assert(source != null)
    assert(source.isDefined)
    source.map(url => contentsOf(Source.fromURL(url)))
  }

  def ofInputStream(source: => InputStream): String = {
    assert(source != null)
    contentsOf(Source.fromInputStream(source))
  }

  def ofInputStream(source: => Option[InputStream]): Option[String] = {
    assert(source != null)
    assert(source.isDefined)
    source.map(is => contentsOf(Source.fromInputStream(is)))
  }

  /**
   * @param source the OutputStream to get the contents of
   * @throws NullPointerException if source is null
   * @throws IOException          if source isn't a ByteArrayOutputStream
   * @return the string contents of the ByteArrayOutputStream
   */
  @throws[NullPointerException]
  @throws[IOException]
  def ofOutputStream(source: => OutputStream): String = {
    assert(source != null)
    source match {
      case baos: ByteArrayOutputStream => baos.toString
      case _ => throw new IOException(s"required: ByteArrayOutputStream but found ${source.getClass.getName}")
    }
  }

  private[this] def contentsOf(source: => BufferedSource): String = {
    try {
      assert(source != null)
      source.mkString
    } finally {
      if (source != null) {
        source.close()
      }
    }
  }
}
