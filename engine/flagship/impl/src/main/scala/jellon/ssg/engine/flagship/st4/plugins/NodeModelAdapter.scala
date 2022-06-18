package jellon.ssg.engine.flagship.st4.plugins

import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.{ListNode, ValueNode}
import org.stringtemplate.v4.{Interpreter, ModelAdaptor, ST}

object NodeModelAdapter extends ModelAdaptor[INode] {
  override def getProperty(interpreter: Interpreter, st: ST, node: INode, property: Any, propertyName: String): AnyRef =
    getProperty(Option(node).getOrElse(INode.empty), property)

  def getProperty(node: INode, property: Any): AnyRef = {
    property match {
      case index: Number =>
        node.children(index.intValue())
      case propertyName: String =>
        if (propertyName.contains('.')) { // TODO: this shouldn't happen, right?
          propertyName
            .split("\\.")
            .foldLeft(node)((n, attr) =>
              n.attribute(attr)
            )
        } else {
          normalize(node.attribute(propertyName))
        }
      case _ =>
        normalize(node.attribute(property))
    }
  }

  private def normalize(node: INode): AnyRef = node match {
    case node: ValueNode =>
      node.valueAs[AnyRef]
    case node: ListNode =>
      // INodeList implements java.util.Iterable and ST4 inherently treats Iterable uniquely
      node.children
    case _ =>
      node
  }
}
