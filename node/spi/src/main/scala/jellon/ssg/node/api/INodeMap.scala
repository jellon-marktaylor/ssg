package jellon.ssg.node.api

import jellon.ssg.node.spi.{Node, NodeMap}

import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

object INodeMap {
  val empty: INodeMap = EmptyNodeMap

  implicit class NodeMapExt(self: INodeMap) {
    def toMap: Map[Any, INode] = self match {
      case INodeMap.empty =>
        ListMap.empty[Any, INode]
      case nodeMap: NodeMap =>
        nodeMap.elements
      case _ => self
        .keySet
        .foldLeft(ListMap.empty[Any, INode])((v, key) => {
          val currentValue = self.apply(key)
          v + (key -> currentValue)
        })
    }

    @inline
    def attributeAs[A](name: Any, clz: Class[A]): A = self
      .apply(name)
      .valueAs[A](clz)

    @inline
    def attributeAs[A: ClassTag](name: Any): A =
      attributeAs[A](name, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    @inline
    def optAttributeAs[A](name: Any, clz: Class[A]): Option[A] = self
      .optAttribute(name)
      .flatMap(_.optValueAs(clz))

    @inline
    def optAttributeAs[A: ClassTag](name: Any): Option[A] =
      optAttributeAs[A](name, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    @inline
    def setAttribute(kv: (Any, INode)): INodeMap =
      new NodeMap(self.toMap + kv)

    @inline
    def setAttribute(key: Any, value: INode): INodeMap =
      new NodeMap(self.toMap + (key -> value))

    @inline
    def setAttribute(key: Any, value: Any): INodeMap =
      new NodeMap(self.toMap + (key -> Node(value)))

    @inline
    def replaceAttribute(key: Any, transform: INode => INode): INodeMap = self
      .setAttribute(key -> transform(self(key)))

    @inline
    def +(kv: (Any, INode)): INodeMap = setAttribute(kv)

    @inline
    def ++(nodes: IterableOnce[(Any, INode)]): INodeMap = {
      nodes
        .iterator
        .foldLeft(self)((acc, keyAndNode) => {
          acc.replaceAttribute(keyAndNode._1, _ ++ keyAndNode._2)
        })
    }

    @inline
    def ++(nodes: INodeMap): INodeMap = ++(nodes.toMap)
  }

  object NodeMapConverter {
    def toNodeMap[A, B](map: collection.Map[A, B]): Map[Any, INode] = map
      .view
      .map(kv =>
        kv._1 -> Node(kv._2)
      )
      .to(ListMap)
  }

  implicit class NodeMapConverter[A, B](self: collection.Map[A, B]) {
    def toNodeMap: Map[Any, INode] = NodeMapConverter.toNodeMap(self)
  }

}

/** keys are typically strings, but by using `Any`, we open up new possibilities */
trait INodeMap {
  def keySet: Set[Any]

  def optAttribute(key: Any): Option[INode]

  def attribute(key: Any): INode = optAttribute(key).getOrElse(INode.empty)

  def apply(key: Any): INode = optAttribute(key).getOrElse(INode.empty)

  def isEmpty: Boolean

  def nonEmpty: Boolean = !isEmpty
}
