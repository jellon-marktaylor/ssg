package jellon.ssg.node.api

import java.util

object EmptyNodeList extends INodeList {
  override def size = 0

  override def optIndex(index: Int): Option[INode] = Option.empty

  override def index(index: Int): INode = INode.empty

  override def apply(index: Int): INode = INode.empty

  override def isEmpty: Boolean = true

  override def nonEmpty: Boolean = false

  override def iterator: util.Iterator[INode] = new util.Iterator[INode] {
    override def hasNext: Boolean =
      false

    override def next: INode =
      throw new NoSuchElementException("EmptyNodeList.iterator.next")
  }

  override def toString: String = "[]"
}
