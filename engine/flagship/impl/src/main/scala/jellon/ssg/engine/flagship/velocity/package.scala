package jellon.ssg.engine.flagship

import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.{ListNode, MapNode, ValueNode}
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.{Velocity, VelocityEngine}
import org.apache.velocity.context.Context
import org.apache.velocity.runtime.RuntimeConstants

import java.io.{OutputStream, OutputStreamWriter, StringWriter, Writer}
import java.util
import java.util.Properties
import scala.jdk.CollectionConverters.{MapHasAsJava, SeqHasAsJava}

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
    // just using a custom VelocityContext isn't enough to get the results we want
    // path of least coding is converting a node to a Map
    val map = toJavaMap(node)
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

  private[this] def toJavaMap(node: INode): java.util.Map[String, AnyRef] = new util.HashMap(node match {
    case mapNode: MapNode =>
      asJava(mapNode)
    case _ =>
      asJava(new MapNode(node.attributes))
  })

  private[this] def asJava(valueNode: ValueNode): AnyRef = valueNode.optValue match {
    case Some(value) =>
      value match {
        case anyRef: AnyRef =>
          anyRef
        case _ =>
          null
      }
    case _ =>
      null
  }

  private[this] def asJava(listNode: ListNode): java.util.List[AnyRef] = listNode
    .elements
    .map(asJava)
    .asJava

  private[this] def asJava(mapNode: MapNode): java.util.Map[String, AnyRef] = mapNode
    .elements
    .view
    .filter(kv =>
      kv._1.isInstanceOf[String]
    )
    .map(kv =>
      (kv._1.asInstanceOf[String], asJava(kv._2))
    )
    .toMap
    .asJava

  private[this] def asJava(node: INode): AnyRef = node match {
    case valueNode: ValueNode =>
      asJava(valueNode)
    case mapNode: MapNode =>
      asJava(mapNode)
    case listNode: ListNode =>
      asJava(listNode)
    case _ =>
      node
  }
}
