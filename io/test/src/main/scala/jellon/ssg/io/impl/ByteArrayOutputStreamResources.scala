package jellon.ssg.io.impl

import jellon.ssg.io.spi.IOutputStreamResources

import java.io.{ByteArrayOutputStream, OutputStream}
import scala.collection.mutable

class ByteArrayOutputStreamResources extends IOutputStreamResources {
  val outputs: mutable.Map[String, ByteArrayOutputStream] =
    mutable.LinkedHashMap.empty[String, ByteArrayOutputStream]

  def clear(): Unit = outputs.clear()

  override def optOutputStream(resource: String): Option[OutputStream] = synchronized {
    val result = new ByteArrayOutputStream()
    outputs.put(resource, result)
    Some(result)
  }
}
