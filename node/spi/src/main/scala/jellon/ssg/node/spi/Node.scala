package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList, INodeMap}

class Node(override val optValue: Option[_ <: AnyRef], override val children: INodeList, override val attributes: INodeMap) extends INode {
  override def asJava: AnyRef = this

  override def equals(other: INode): Boolean =
    optValue == other.optValue &&
      children == other.children &&
      attributes == other.attributes

  override lazy val toString: String =
    s"\"node\": { ${optValue.map(v => s"\"value\": \"$v\", ")}\"children\": $children, \"attributes\": $attributes"
}

object Node {
  @inline
  def empty: INode =
    INode.empty

  @inline
  def apply(optValue: Option[_ <: AnyRef], children: INodeList, attributes: INodeMap): INode =
    new Node(optValue, children, attributes)
}
