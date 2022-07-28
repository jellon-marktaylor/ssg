package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList}

final class ListNode(override val children: INodeList) extends INode {
  def this(elements: Seq[INode]) =
    this(new NodeList(elements))

  override def asJava: java.util.List[Object] = this
    .children
    .asJava

  override def toSeq: Seq[INode] = this
    .children
    .toSeq

  def elements: Seq[INode] = this
    .toSeq

  override def equals(other: INode): Boolean =
    children == other.children

  override lazy val toString: String =
    children.toString
}

object ListNode {
  lazy val empty: ListNode =
    new ListNode(INodeList.empty)

  @inline
  def apply(children: INodeList): ListNode =
    new ListNode(children)

  @inline
  def apply(elements: Seq[INode]): ListNode =
    new ListNode(elements)

  @inline
  def of(children: IterableOnce[_]): ListNode =
    apply(INodeList.fromSeq(children))
}
