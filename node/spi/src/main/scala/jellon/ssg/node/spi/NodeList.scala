package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList}

final class NodeList(override val elements: Seq[INode]) extends INodeList {
  override def toSeq: Seq[INode] =
    elements

  override def optIndex(index: Int): Option[INode] =
    elements.unapply(index)

  override def index(index: Int): INode =
    elements.applyOrElse[Int, INode](index, _ => INode.empty)

  override def apply(index: Int): INode =
    elements.applyOrElse[Int, INode](index, _ => INode.empty)

  override def equals(obj: Any): Boolean = obj match {
    case other: INodeList =>
      elements == other.toSeq
    case _ => false
  }

  override lazy val toString: String =
    elements.mkString("[ ", ", ", " ]")
}

object NodeList {
  @inline
  def empty: INodeList =
    INodeList.empty

  @inline
  def apply(elements: Seq[INode]): NodeList =
    new NodeList(elements)
}
