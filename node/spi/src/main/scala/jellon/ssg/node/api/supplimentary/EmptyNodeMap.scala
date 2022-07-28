package jellon.ssg.node.api.supplimentary

import jellon.ssg.node.api.{INode, INodeMap}

object EmptyNodeMap extends INodeMap {
  override def toMap: Map[AnyRef, INode] = Map.empty

  override def keySet: Set[AnyRef] = Set.empty

  override def size: Int = 0

  override def isEmpty: Boolean = true

  override def nonEmpty: Boolean = false

  override def apply(key: Any): INode = INode.empty

  override def attribute(key: Any): INode = INode.empty

  override def optAttribute(key: Any): Option[INode] = Option.empty

  override def setAttributes(elements: INodeMap): INodeMap = elements

  override def toString: String = "{}"
}
