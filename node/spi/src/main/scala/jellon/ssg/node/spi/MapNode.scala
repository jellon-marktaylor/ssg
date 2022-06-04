package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList, INodeMap}

final class MapNode private (val elements: Map[Any, INode], val attributes: INodeMap) extends INode {
  def this(children: INodeMap) = this(children.toMap, children)

  def this(elements: Map[Any, INode]) = this(elements, new NodeMap(elements))

  override val optValue: None.type = None

  override val children: INodeList = INodeList.empty

  override def equals(other: INode): Boolean =
    attributes == other.attributes

  override lazy val toString: String = attributes.toString
}
