package jellon.ssg.node.api

import jellon.ssg.node.api.supplimentary.EmptyNode
import jellon.ssg.node.spi._

import java.util.Optional
import scala.collection.MapView
import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}

trait INode extends IValueNode[INode] with IParentNode[INode] with IObjectNode[INode] {
  def asJava: Object

  def equals(other: INode): Boolean

  override def equals(obj: Any): Boolean =
    obj match {
      case other: INode =>
        equals(other)
      case _ =>
        false
    }

  /////////////
  // IValueNode
  /////////////

  override def optValue: Option[_ <: AnyRef] = Option.empty

  override def setValue(optValue: Option[_ <: AnyRef]): INode =
    Node(optValue, children, attributes)

  // this is intentionally left blank

  /////////////
  // IListNode
  /////////////

  override def toSeq: Seq[INode] =
    Seq.empty

  override def children: INodeList =
    INodeList.empty

  def hasChildren: Boolean = this
    .children
    .nonEmpty

  override def setChildren(children: INodeList): INode =
    Node(optValue, children, attributes)

  /////////////
  // IMapNode
  /////////////

  override def toMap: Map[AnyRef, INode] =
    Map.empty

  override def attributes: INodeMap =
    INodeMap.empty

  def hasAttributes: Boolean =
    false

  override def keySet: Set[AnyRef] =
    Set.empty

  override def optAttribute(name: Any): Option[INode] =
    Option.empty

  override def setAttributes(attributes: INodeMap): INode =
    Node(optValue, children, attributes)
}

object INode {
  val empty: INode = EmptyNode

  implicit class INodeExt[A <: INode](self: A) {
    @inline
    def merge(other: INode): INode =
      if (INode.empty == self) other
      else new Node(
        mergeOptions(self.optValue, other.optValue),
        self.children ++ other.children,
        self.attributes ++ other.attributes
      )

    @inline
    def ++(other: INode): INode = merge(other)
  }

  /**
   * @param lhs left-hand-side (first value)
   * @param rhs right-hand-side (second value)
   * @return if both have a value, Seq(lhs, rhs); if neither have a value, return empty; else return the one with a value
   */
  def mergeOptions(lhs: Option[_], rhs: Option[_]): Option[AnyRef] =
    if (lhs.isEmpty)
      rhs.map(_.asInstanceOf[AnyRef])
    else if (rhs.isEmpty)
      lhs.map(_.asInstanceOf[AnyRef])
    else
      Option(mergeValues(lhs.get, rhs.get))

  /**
   * @param lhs left-hand-side (first value)
   * @param rhs right-hand-side (second value)
   */
  def mergeValues(lhs: Any, rhs: Any): AnyRef = {
    lhs match {
      case lhsSeq: Seq[_] =>
        rhs match {
          case rhsSeq: Seq[_] =>
            lhsSeq ++ rhsSeq
          case _ =>
            lhsSeq.appended(rhs)
        }
      case _ =>
        rhs match {
          case rhsSeq: Seq[_] =>
            rhsSeq.prepended(lhs)
          case _ =>
            Vector(lhs, rhs)
        }
    }
  }

  def apply(optValue: Option[_ <: AnyRef], children: INodeList, attributes: INodeMap): INode =
    if (optValue.isEmpty && children.isEmpty) {
      INode(attributes)
    } else if (optValue.isEmpty && attributes.isEmpty) {
      INode(children)
    } else if (children.isEmpty && attributes.isEmpty) {
      INode(optValue)
    } else {
      new Node(optValue, children, attributes)
    }

  @inline
  def apply(children: INodeList): INode =
    new ListNode(children)

  @inline
  def apply(children: Seq[INode]): INode =
    new ListNode(children)

  @inline
  def apply(attributes: INodeMap): INode =
    MapNode(attributes)

  @inline
  def apply(attributes: Map[AnyRef, INode]): INode =
    MapNode(attributes)

  @inline
  def of(attributes: Map[_, _]): INode =
    MapNode.of(attributes)

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
      INode(opt)
    case opt: Optional[_] =>
      apply(opt)
    case map: Map[_, _] =>
      MapNode.of(map)
    case mapView: MapView[_, _] =>
      MapNode.of(mapView.to(ListMap))
    case children: IterableOnce[_] =>
      ListNode.of(children)
    case javaMap: java.util.Map[_, _] =>
      MapNode.of(javaMap.asScala.to(ListMap))
    case javaList: java.util.Collection[_] =>
      ListNode.of(javaList.asScala.toList)
    case _ =>
      INode(Some(value))
  }
}
