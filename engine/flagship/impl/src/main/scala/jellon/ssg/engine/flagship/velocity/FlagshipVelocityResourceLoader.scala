package jellon.ssg.engine.flagship.velocity

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.io.spi.IInputStreamResources
import org.apache.velocity.runtime.resource.Resource
import org.apache.velocity.runtime.resource.loader.ResourceLoader
import org.apache.velocity.util.ExtProperties

import java.io.{InputStreamReader, Reader}

class FlagshipVelocityResourceLoader(resources: IInputStreamResources) extends ResourceLoader {
  def this(engine: IFlagshipEngine) =
    this(engine.resources)

  override def init(configuration: ExtProperties): Unit = {}

  override def getResourceReader(source: String, encoding: String): Reader = {
    resources.optInputStream(source) match {
      case Some(inputStream) =>
        new InputStreamReader(inputStream, encoding)
      case _ =>
        // null is of the devil, but we have no option when integrating with libraries that use it
        null
    }
  }

  override def isSourceModified(resource: Resource): Boolean =
    resource.isSourceModified

  override def getLastModified(resource: Resource): Long =
    resource.getLastModified
}
