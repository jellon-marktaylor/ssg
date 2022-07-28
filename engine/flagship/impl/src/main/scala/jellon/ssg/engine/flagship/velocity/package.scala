package jellon.ssg.engine.flagship

import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.{Velocity, VelocityEngine}
import org.apache.velocity.context.Context
import org.apache.velocity.runtime.RuntimeConstants

import java.io.{OutputStream, OutputStreamWriter, StringWriter, Writer}
import java.util.Properties
import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.MapHasAsJava

package object velocity {
  def merge(inputResources: IInputStreamResources, templateName: String, node: INode, writer: Writer): Unit = {
    val context = createVelocityContext(node)
    createVelocityEngine(inputResources)
      .getTemplate(templateName)
      .merge(context, writer)
  }

  def merge(inputResources: IInputStreamResources, templateName: String, node: INode, outputStream: OutputStream): Unit = {
    val writer = new OutputStreamWriter(outputStream)
    try {
      merge(inputResources, templateName, node, writer)
    } finally {
      writer.flush()
      writer.close()
    }
  }

  def merge(inputResources: IInputStreamResources, templateName: String, node: INode): String = {
    val writer = new StringWriter
    merge(inputResources, templateName, node, writer)
    writer.toString
  }

  def merge(templateString: String, node: INode): String = {
    val context = createVelocityContext(node)
    val writer = new StringWriter
    Velocity.evaluate(context, writer, "merge", templateString)
    writer.toString
  }

  private[this] def createVelocityContext(node: INode): Context = {
    // VelocityContext integrates with a java.util.Map by default
    // better to convert to what it knows how to use than create a custom handler
    val map = node
      .toMap
      .view
      .filterKeys(_.isInstanceOf[String])
      .map[(String, Object)](
        kv => kv._1.asInstanceOf[String] -> kv._2.asJava
      )
      .to(ListMap)
      .asJava
    new VelocityContext(map)
  }

  private[this] def createVelocityEngine(inputResources: IInputStreamResources): VelocityEngine = {
    val properties = createVelocityProperties(inputResources)
    val velocityEngine = new VelocityEngine()
    velocityEngine.init(properties)

    velocityEngine
  }

  private[this] def createVelocityProperties(inputResources: IInputStreamResources): Properties = {
    val name: String = "flagship"
    val properties: Properties = new Properties()
    properties.setProperty(RuntimeConstants.RESOURCE_LOADERS, name)
    properties.put(
      s"${RuntimeConstants.RESOURCE_LOADER}.$name.${RuntimeConstants.RESOURCE_LOADER_INSTANCE}",
      new FlagshipVelocityResourceLoader(inputResources)
    )

    properties
  }
}
