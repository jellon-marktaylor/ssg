package jellon.ssg.node.api

object EmptyNodeMap extends INodeMap {
  override def keySet: Set[Any] = Set.empty

  override def optAttribute(key: Any): Option[INode] = Option.empty

  override def attribute(key: Any): INode = INode.empty

  override def apply(key: Any): INode = INode.empty

  override def isEmpty: Boolean = true

  override def nonEmpty: Boolean = false

  override def toString: String = "{}"
}
