package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{IFlagshipNodeMapExtensions, INSTRUCTIONS}
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.io.spi.IOutputStreamResources
import jellon.ssg.node.api.INodeMap

import java.io.{File, IOException, InputStream}
import java.util.Objects
import java.util.zip.ZipInputStream

object UnzipNodeProcessor extends AbstractNodeProcessor("unzip") {
  override def processAttributes(unzipNode: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
    val from: InputStream = engine.readFrom(engine.resolveNodeString(state, s"${INSTRUCTIONS}.from"))
    try {
      val to = engine.resolveNodeString(state, s"${INSTRUCTIONS}.to")
      unzip(from, to, engine.resources)
    } finally if (from != null) from.close()

    INodeMap.empty
  }

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
