package jellon.ssg.node.api.supplimentary

import jellon.ssg.node.api.INode

import java.util
import scala.jdk.CollectionConverters.SeqHasAsJava

object ListHelper {
  def toNodeIterator(elements: IterableOnce[_]): Iterator[INode] =
    elements.iterator.map(INode(_))

  implicit class NodeIterableConverter(self: IterableOnce[_]) {
    def toNodeIterator: Iterator[INode] =
      ListHelper.toNodeIterator(self)
  }

  @inline
  def toNodeIteratorJava(elements: Seq[INode]): java.util.Iterator[INode] =
    new SeqIteratorWrapper(elements.iterator)

  private class SeqIteratorWrapper(delegate: scala.collection.Iterator[INode]) extends java.util.Iterator[INode] {
    override def hasNext: Boolean = delegate.hasNext

    override def next(): INode = delegate.next
  }

  lazy val emptyIterator: util.Iterator[INode] = new util.Iterator[INode] {
    override def hasNext: Boolean =
      false

    override def next: INode =
      throw new NoSuchElementException("EmptyNodeList.iterator.next")
  }

  def asJava(elements: Seq[INode]): java.util.List[AnyRef] = elements
    .map(
      _.asJava
    )
    .asJava
}
