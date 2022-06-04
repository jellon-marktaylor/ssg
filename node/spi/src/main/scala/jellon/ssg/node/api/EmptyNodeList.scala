package jellon.ssg.node.api

object EmptyNodeList extends INodeList {
  override def size = 0

  override def optIndex(index: Int): Option[INode] = Option.empty

  override def index(index: Int): INode = INode.empty

  override def apply(index: Int): INode = INode.empty

  override def isEmpty: Boolean = true

  override def nonEmpty: Boolean = false

  override def toString: String = "[]"
}
