package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.io.spi.IOutputStreamResources
import jellon.ssg.node.api.{INode, INodeMap}

import java.io.{File, IOException, InputStream}
import java.util.Objects
import java.util.zip.ZipInputStream

/**
 * unzip <= name
 *
 * @note from (required) the resource path and name of the vm file to use including the extension
 * @note to (required) the file or directory (if hint is used) to write to
 * @note resolver (optional) if provided, uses the named resolver to resolve strings
 */
object UnzipNodeProcessor extends AbstractNodeProcessor("unzip") {
  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): Unit = {
    val resolverName =
      node.optAttributeAs[String]("resolver")
    val to =
      resolve("to", resolverName, engine, state, node)
    val fromString =
      resolve("from", resolverName, engine, state, node)
    val from: InputStream =
      engine.readFrom(fromString)
    try {
      unzip(from, to, engine.resources)
    } finally if (from != null) from.close()
  }

  private[this] def resolve(attributeName: String, resolverName: Option[String], engine: IFlagshipEngine, state: INodeMap, node: INode): String =
    resolverName
      .map(resolver =>
        engine.resolveNodeString(resolver, state, node.attributeAs[String](attributeName))
      )
      .getOrElse(node.attributeAs[String](attributeName))

  @throws[IOException]
  private def unzip(from: InputStream, to: String, resourceLocator: IOutputStreamResources): Unit = {
    // TODO: this code was auto converted from Java to Scala and is not idiomatic
    Objects.requireNonNull(from, "from")
    Objects.requireNonNull(to, "to")
    Objects.requireNonNull(resourceLocator)
    val zis = new ZipInputStream(from)
    try {
      val buffer = new Array[Byte](1024) // magic number buffer size
      var len = 0
      var zipEntry = zis.getNextEntry
      while (zipEntry != null) {
        if (!zipEntry.isDirectory) try {
          val fileName = zipEntry.getName
          val os = resourceLocator.openOutputStream(new File(to, fileName).getPath)
          try {
            Objects.requireNonNull(os, () => "No such resource: " + fileName)
            len = zis.read(buffer)
            while (len > 0) {
              os.write(buffer, 0, len)
              len = zis.read(buffer)
            }
          } finally {
            if (os != null)
              os.close()
          }
        } finally {
          zis.closeEntry()
        }

        zipEntry = zis.getNextEntry
      }
    } finally {
      if (zis != null)
        zis.close()
    }
  }
}
