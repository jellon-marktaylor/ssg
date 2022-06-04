package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList}

final class NodeList(val elements: Seq[INode]) extends INodeList {
  override lazy val size: Int = elements.size

  override def optIndex(index: Int): Option[INode] =
    if (elements.isDefinedAt(index)) Some(elements(index))
    else Option.empty

  override def index(index: Int): INode = elements.applyOrElse[Int, INode](index, _ => INode.empty)

  override def apply(index: Int): INode = elements.applyOrElse[Int, INode](index, _ => INode.empty)

  override def isEmpty: Boolean = elements.isEmpty

  override def nonEmpty: Boolean = elements.nonEmpty

  override def equals(obj: Any): Boolean = obj match {
    case other: NodeList =>
      elements == other.elements
    case other: INodeList =>
      elements == other.toSeq
    case _ => false
  }

  override lazy val toString: String = elements.mkString("[ ", ", ", " ]")
}
