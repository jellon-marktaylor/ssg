package jellon.ssg.io.impl

import jellon.ssg.io.IResetable
import jellon.ssg.io.spi.IOutputStreamResources

import java.io.{ByteArrayOutputStream, OutputStream}
import scala.collection.mutable

/**
 * a stateless singleton that always returns a ByteArrayOutputStream
 *
 * @see [[ByteArrayOutputStreamResources]] class if you need to retrieve the written resource
 */
object ByteArrayOutputStreamResources extends IOutputStreamResources {
  override def optOutputStream(resource: String): Option[OutputStream] =
    Some(new ByteArrayOutputStream())

  override def openOutputStream(resource: String): OutputStream =
    new ByteArrayOutputStream()
}

/**
 * always returns a ByteArrayOutputStream and saves it in a mutable Map with the resource name for later lookup
 *
 * @see [[ByteArrayOutputStreamResources]] object if you don't need to retrieve the written resource
 */
class ByteArrayOutputStreamResources extends IOutputStreamResources with IResetable {
  val outputs: mutable.Map[String, ByteArrayOutputStream] =
    mutable.LinkedHashMap.empty[String, ByteArrayOutputStream]

  override def reset(): Unit =
    outputs.clear()

  override def optOutputStream(resource: String): Option[OutputStream] = synchronized {
    val result = new ByteArrayOutputStream()
    outputs.put(resource, result)
    Some(result)
  }
}
