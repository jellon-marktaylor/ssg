package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeMap}

import scala.collection.immutable.ListMap

final class NodeMap(override val elements: Map[AnyRef, INode]) extends INodeMap {
  def this(kv: (AnyRef, INode)) =
    this(ListMap(kv))

  def this(key: AnyRef, value: INode) =
    this(key -> value)

  def this(key: Any, value: Any) =
    this(key.asInstanceOf[AnyRef] -> INode(value))

  override def toMap: Map[AnyRef, INode] =
    elements

  override def setAttributes(other: INodeMap): INodeMap =
    other

  override def equals(obj: Any): Boolean = obj match {
    case other: NodeMap =>
      elements == other.elements
    case other: INodeMap =>
      elements == other.toMap
    case _ => false
  }

  override def toString: String = s"${elements.map(kv => s"\"${kv._1.toString}\": ${kv._2}").mkString("{ ", ", ", " }")}"
}

object NodeMap {
  @inline
  def apply(elements: Map[AnyRef, INode]): NodeMap =
    new NodeMap(elements)

  @inline
  def apply(entries: (_, _)*): NodeMap =
    new NodeMap(entries
      .view
      .map(
        kv => kv._1.asInstanceOf[AnyRef] -> INode(kv._2)
      )
      .to(ListMap)
    )

  @inline
  def apply(kv: (AnyRef, INode)): NodeMap =
    new NodeMap(kv)

  @inline
  def apply(key: AnyRef, value: INode): NodeMap =
    new NodeMap(key, value)

  @inline
  def apply(key: Any, value: Any): NodeMap =
    new NodeMap(key, value)

  @inline
  def of(map: Map[_, _]): NodeMap =
    new NodeMap(map
      .view
      .map(
        kv => kv._1.asInstanceOf[AnyRef] -> INode(kv._2)
      )
      .to(ListMap)
    )

  @inline
  def of(kv: (_, _)): NodeMap = this
    .apply(kv._1.asInstanceOf[AnyRef] -> INode(kv._2))
}
