package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.engine.flagship.velocity
import jellon.ssg.io.api.emptyString
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node

import java.io.{Closeable, IOException, OutputStream}

/**
 * velocity <= name
 * @note template (required) the resource path and name of the vm file to use including the extension
 * @note output (required) the file or directory (if hint is used) to write to
 * @note hint (optional) to dynamically pick the output file name (eg. java, csharp)
 * @note using (optional defaults to output) name of the defined state to pass to velocity
 * @note resolver (optional defaults to velocity) to resolve references in the instruction file
 */
object VelocityNodeProcessor extends AbstractNodeProcessor("velocity") {
  @throws[IOException]
  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, velocityNode: INode): Unit = {
    val properties: INodeMap =
      state(name).attributes ++ velocityNode.attributes
    val resolverName: String = properties
      .optString("resolver")
      .getOrElse(name) // default to velocity
    val templateName: String = properties
      .string("template")
    val attributes: INode =
      resolvedAttributes(state, properties, resolverName, engine)

    val output = openOutput(state, properties, resolverName, engine)
    tryWith(output)(
      outputStream => {
        val resources = engine.resources
        velocity.merge(resources, templateName, attributes, outputStream)
      }
    )
  }

  private[this] def resolvedAttributes(state: INodeMap, properties: INodeMap, resolverName: String, engine: IFlagshipEngine): INode = {
    val usingString = properties
      .optString("using")
      .filterNot(_ == "")

    val attributes: INodeMap = usingString
      .map(usingString =>
        engine.resolve(resolverName, state, usingString)
      )
      .map(usingString =>
        state(usingString).attributes
      )
      .getOrElse(state)

    val resolvedAttributes =
      engine.resolver.resolveStringAttributes(name, state, attributes)

    Node(resolvedAttributes)
  }

  private[this] def openOutput(state: INodeMap, properties: INodeMap, resolverName: String, engine: IFlagshipEngine): OutputStream = {
    val output = engine.resolve(resolverName, state, properties.string("output"))
    val hint = properties.optString("hint")
      .getOrElse(emptyString)
    engine.writeTo(output, hint)
  }

  private[this] def tryWith[A <: Closeable](closable: A)(f: A => Unit): Unit = {
    try {
      f.apply(closable)
    } finally {
      if (closable != null) {
        closable.close()
      }
    }
  }
}
