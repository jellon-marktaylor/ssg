package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList, INodeMap}

final class ListNode private(val elements: Seq[INode], val children: INodeList) extends INode {
  def this(children: INodeList) = this(children.toSeq, children)

  def this(elements: Seq[INode]) = this(elements, new NodeList(elements))

  override val optValue: None.type = None

  override val attributes: INodeMap = INodeMap.empty

  override def equals(other: INode): Boolean =
    children == other.children

  override lazy val toString: String = children.toString
}
