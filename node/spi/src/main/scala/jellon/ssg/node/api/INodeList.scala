package jellon.ssg.node.api

import jellon.ssg.node.spi.{Node, NodeList}

object INodeList {
  val empty: INodeList = EmptyNodeList

  implicit class NodeListExt(self: INodeList) {
    def toSeq: Seq[INode] = self match {
      case nodeList: NodeList => nodeList.elements
      case _ => Range(0, self.size).foldLeft(Vector.empty[INode])((v, index) => {
        val node = self.optIndex(index)
        if (node.isDefined) v :+ node.get
        else v
      })
    }

    @inline
    def setChildren(children: Seq[INode]): INodeList =
      new NodeList(children)

    @inline
    def addChild(child: INode): INodeList =
      new NodeList(self.toSeq :+ child)

    @inline
    def :+(child: INode): INodeList = addChild(child)

    @inline
    def addChildren(children: IterableOnce[INode]): INodeList =
      setChildren(self.toSeq ++ children)

    @inline
    def ++(nodes: IterableOnce[INode]): INodeList =
      addChildren(nodes)

    @inline
    def ++(nodes: INodeList): INodeList =
      addChildren(nodes.toSeq)
  }

  object NodeIterableConverter {
    def toNodeIterator(elements: IterableOnce[_]): Iterator[INode] = elements.iterator.map(Node(_))
  }

  implicit class NodeIterableConverter(self: IterableOnce[_]) {
    def toNodeIterator: Iterator[INode] = NodeIterableConverter.toNodeIterator(self)
  }

}

trait INodeList {
  def size: Int

  def optIndex(index: Int): Option[INode]

  def index(index: Int): INode = optIndex(index).getOrElse(INode.empty)

  def apply(index: Int): INode = optIndex(index).getOrElse(INode.empty)

  def isEmpty: Boolean

  def nonEmpty: Boolean = !isEmpty
}
