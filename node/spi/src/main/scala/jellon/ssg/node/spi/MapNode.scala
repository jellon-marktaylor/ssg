package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeMap}

final class MapNode(override val attributes: INodeMap) extends INode {
  def this(elements: Map[AnyRef, INode]) =
    this(NodeMap(elements))

  def this(entries: (_, _)*) =
    this(NodeMap(entries: _*))

  def this(kv: (AnyRef, INode)) =
    this(NodeMap(kv))

  def this(key: AnyRef, value: INode) =
    this(NodeMap(key, value))

  def this(key: Any, value: Any) =
    this(NodeMap(key, value))

  def asJava: java.util.Map[Object, Object] = this
    .attributes
    .asJava

  override def toMap: Map[AnyRef, INode] = this
    .attributes
    .toMap

  def elements: Map[AnyRef, INode] = this
    .toMap

  override def equals(other: INode): Boolean =
    attributes == other.attributes

  override lazy val toString: String =
    attributes.toString
}

object MapNode {
  @inline
  def apply(attributes: INodeMap): MapNode =
    new MapNode(attributes)

  @inline
  def apply(elements: Map[AnyRef, INode]): MapNode =
    new MapNode(elements)

  @inline
  def apply(entries: (_, _)*): MapNode =
    new MapNode(entries: _*)

  @inline
  def apply(kv: (AnyRef, INode)): MapNode =
    new MapNode(kv)

  @inline
  def apply(key: AnyRef, value: INode): MapNode =
    new MapNode(key, value)

  @inline
  def apply(key: Any, value: Any): MapNode =
    new MapNode(key, value)

  @inline
  def of(map: Map[_, _]): MapNode =
    new MapNode(NodeMap.of(map))

  @inline
  def of(kv: (_, _)): MapNode =
    new MapNode(NodeMap.of(kv))
}
