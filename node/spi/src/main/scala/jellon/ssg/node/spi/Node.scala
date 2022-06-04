package jellon.ssg.node.spi

import jellon.ssg.node.api.INodeMap.NodeMapConverter
import jellon.ssg.node.api.{INode, INodeList, INodeMap}

import java.util.Optional
import scala.collection.MapView
import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}

class Node(val optValue: Option[_], val children: INodeList, val attributes: INodeMap) extends INode {
  override def equals(other: INode): Boolean =
    optValue == other.optValue &&
      children == other.children &&
      attributes == other.attributes

  override lazy val toString: String = s"\"node\": { ${optValue.map(v => s"\"value\": \"$v\", ")}\"children\": $children, \"attributes\": $attributes"
}

object Node {
  val empty: INode = INode.empty

  def apply(optValue: Option[_], children: INodeList, attributes: INodeMap): INode =
    if (optValue.isEmpty && children.isEmpty) {
      Node(attributes)
    } else if (optValue.isEmpty && attributes.isEmpty) {
      Node(children)
    } else if (children.isEmpty && attributes.isEmpty) {
      Node(optValue)
    } else {
      new Node(optValue, children, attributes)
    }

  @inline
  def apply(children: INodeList): INode =
    new ListNode(children)

  @inline
  def apply(children: Seq[INode]): INode =
    new ListNode(new NodeList(children))

  @inline
  def ListNode(children: IterableOnce[_]): INode = Node(new NodeList(children.iterator.map(element => Node(element)).toSeq))

  @inline
  def apply(attributes: INodeMap): INode =
    new MapNode(attributes)

  @inline
  def apply(attributes: Map[Any, INode]): INode =
    new MapNode(new NodeMap(attributes))

  @inline
  def MapNode(attributes: collection.Map[_, _]): INode =
    new MapNode(new NodeMap(attributes.toNodeMap))

  @inline
  def apply(optValue: Option[_]): INode =
    if (optValue.isEmpty) empty
    else new ValueNode(optValue)

  @inline
  def apply(optValue: Optional[_]): INode =
    if (optValue.isPresent) new ValueNode(optValue)
    else empty

  @inline
  def apply(value: Any): INode = value match {
    case null =>
      empty
    case node: INode =>
      node
    case opt: Option[_] =>
      Node(opt)
    case opt: Optional[_] =>
      if (opt.isPresent) Node(Some(opt.get))
      else empty
    case map: collection.Map[_, _] =>
      MapNode(map)
    case mapView: MapView[_, _] =>
      MapNode(mapView.toMap)
    case children: IterableOnce[_] =>
      ListNode(children)
    case javaMap: java.util.Map[_, _] =>
      MapNode(javaMap.asScala)
    case javaList: java.util.Collection[_] =>
      ListNode(javaList.asScala.toList)
    case _ =>
      Node(Some(value))
  }
}
