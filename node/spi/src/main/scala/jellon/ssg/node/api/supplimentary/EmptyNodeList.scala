package jellon.ssg.node.api.supplimentary

import jellon.ssg.node.api.{INode, INodeList}

import java.util

object EmptyNodeList extends INodeList {
  override def toSeq: Seq[INode] = Seq.empty

  override def size = 0

  override def isEmpty: Boolean = true

  override def nonEmpty: Boolean = false

  override def setChildren(children: INodeList): INodeList = children

  override def optIndex(index: Int): Option[INode] = Option.empty

  override def index(index: Int): INode = INode.empty

  override def apply(index: Int): INode = INode.empty

  override def iterator: util.Iterator[INode] = ListHelper.emptyIterator

  override def toString: String = "[]"
}
