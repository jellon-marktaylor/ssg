package jellon.ssg.node.api

import jellon.ssg.node.api.supplimentary.{EmptyNodeMap, MapHelper}
import jellon.ssg.node.spi.NodeMap

import java.util

/** keys are typically strings, but by using `Any`, we open up new possibilities */
trait INodeMap extends IObjectNode[INodeMap] {
  override def attributes: INodeMap = this

  def asJava: util.Map[Object, Object] =
    MapHelper.asJava(elements)

  def elements: Map[AnyRef, INode] = this
    .toMap

  def size: Int = this
    .elements
    .size

  def isEmpty: Boolean = this
    .elements
    .isEmpty

  def nonEmpty: Boolean = this
    .elements
    .nonEmpty

  def apply(key: Any): INode = optAttribute(key)
    .getOrElse(INode.empty)

  // TODO: identify and override methods in IObjectNode where it makes sense to

  @inline
  def +(kv: (Any, INode)): INodeMap = setAttribute(kv)

  @inline
  def ++(nodes: INodeMap): INodeMap = ++(nodes.toMap)

  @inline
  def ++(nodes: IterableOnce[(Any, INode)]): INodeMap = {
    nodes
      .iterator
      .foldLeft[INodeMap](this)((acc, keyAndNode) => {
        acc.mergeAttribute(keyAndNode._1, keyAndNode._2)
      })
  }
}

object INodeMap {
  val empty: INodeMap =
    EmptyNodeMap

  @inline
  def apply(elements: Map[AnyRef, INode]): INodeMap =
    NodeMap(elements)

  @inline
  def apply(entries: (_, _)*): INodeMap =
    NodeMap(entries: _*)

  @inline
  def apply(kv: (AnyRef, INode)): INodeMap =
    NodeMap(kv)

  @inline
  def apply(key: AnyRef, value: INode): INodeMap =
    NodeMap(key, value)

  @inline
  def apply(key: Any, value: Any): INodeMap =
    NodeMap(key, value)

  @inline
  def of(map: Map[_, _]): INodeMap =
    NodeMap.of(map)

  @inline
  def of(kv: (_, _)): INodeMap =
    NodeMap.of(kv)
}
