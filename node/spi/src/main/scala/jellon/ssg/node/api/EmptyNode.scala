package jellon.ssg.node.api

object EmptyNode extends INode {
  def optValue: Option[_] = Option.empty

  def children: INodeList = INodeList.empty

  def attributes: INodeMap = INodeMap.empty

  override def equals(other: INode): Boolean =
    this.eq(other)

  override def toString = "{}"
}
