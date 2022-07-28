package jellon.ssg.node.api

import jellon.ssg.node.api.supplimentary.{EmptyNodeList, ListHelper}
import jellon.ssg.node.spi.NodeList

trait INodeList extends IParentNode[INodeList] with java.lang.Iterable[INode] {
  override def children: INodeList = this

  def asJava: java.util.List[Object] =
    ListHelper.asJava(toSeq)

  def elements: Seq[INode] = this
    .toSeq

  def size: Int = this
    .elements
    .size

  def isEmpty: Boolean = this
    .elements
    .isEmpty

  def nonEmpty: Boolean = this
    .elements
    .nonEmpty

  def apply(index: Int): INode =
    optIndex(index).getOrElse(INode.empty)

  override def setChildren(children: INodeList): INodeList =
    children

  def :+(child: INode): INodeList = addChild(child)

  def ++(nodes: INodeList): INodeList =
    addChildren(nodes.toSeq)

  def ++(nodes: IterableOnce[INode]): INodeList =
    addChildren(nodes)

  override def iterator: java.util.Iterator[INode] =
    ListHelper.toNodeIteratorJava(toSeq)
}

object INodeList {
  val empty: INodeList =
    EmptyNodeList

  def apply(elements: Seq[INode]): INodeList =
    new NodeList(elements)

  @inline
  def fromSeq(children: IterableOnce[_]): INodeList =
    apply(children.iterator.map(element => INode(element)).toSeq)
}
