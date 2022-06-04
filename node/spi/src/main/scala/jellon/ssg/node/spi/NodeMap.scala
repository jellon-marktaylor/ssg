package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeMap}

import scala.collection.immutable.ListMap

final class NodeMap(val elements: Map[Any, INode]) extends INodeMap {
  def this(key: Any, value: Any) =
    this(ListMap[Any, INode](key -> Node(value)))

  def this(kv: (Any, INode)) =
    this(ListMap[Any, INode](kv))

  override def keySet: Set[Any] = elements.keySet

  override def optAttribute(key: Any): Option[INode] = elements.get(key)

  override def apply(key: Any): INode = elements.getOrElse(key, INode.empty)

  override def isEmpty: Boolean = elements.isEmpty

  override def nonEmpty: Boolean = elements.nonEmpty

  override def equals(obj: Any): Boolean = obj match {
    case other: NodeMap =>
      elements == other.elements
    case other: INodeMap =>
      elements == other.toMap
    case _ => false
  }

  override def toString: String = s"${elements.map(kv => s"\"${kv._1.toString}\": ${kv._2}").mkString("{ ", ", ", " }")}"
}
