package jellon.ssg.node.api

import jellon.ssg.node.api.INodeMap.NodeMapExt
import jellon.ssg.node.spi.Node

import scala.reflect.ClassTag

object INode {
  val empty: INode = EmptyNode

  implicit class NodeExt[A <: INode](self: A) {
    // VALUE
    @inline
    def hasValue: Boolean = self
      .optValue
      .isDefined

    @inline
    def optValueAs[A](clz: Class[A]): Option[A] = self
      .optValue
      .filter(clz.isInstance)
      .map(clz.cast)

    @inline
    def optValueAs[A: ClassTag]: Option[A] =
      optValueAs(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    @inline
    @throws[NoSuchElementException]
    def value: Any = self.optValue match {
      case Some(value) => value
      case None => throw new NoSuchElementException(this + " is empty")
    }

    @inline
    def valueAs[A](clz: Class[A]): A = optValueAs[A](clz) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(this + " does not contain a value of type " + clz.getName)
    }

    @inline
    def valueAs[A: ClassTag]: A =
      valueAs(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    @inline
    def setValue(optValue: Option[_]): INode =
      Node(optValue, self.children, self.attributes)

    @inline
    def setValue(value: Any): INode = value match {
      case option: Option[_] => setValue(option)
      case notOption: Any => setValue(Option(notOption))
    }

    // CHILDREN
    @inline
    def hasChildren: Boolean = self
      .children
      .nonEmpty

    @inline
    def optIndex(index: Int): Option[INode] = self
      .children
      .optIndex(index)

    @inline
    def index(index: Int): INode = self
      .children
      .apply(index)

    @inline
    def setChildren(children: INodeList): INode =
      Node(self.optValue, children, self.attributes)

    @inline
    def addChild(child: INode): INode =
      setChildren(self.children.addChild(child))

    @inline
    def addChildren(children: IterableOnce[INode]): INode =
      setChildren(self.children.addChildren(children))

    // ATTRIBUTES
    @inline
    def hasAttributes: Boolean = self
      .attributes
      .nonEmpty

    @inline
    def keySet: Set[Any] = self
      .attributes
      .keySet

    @inline
    def attribute(name: Any): INode = self
      .attributes
      .attribute(name)

    @inline
    def attributeAs[A](name: Any, clz: Class[A]): A = self
      .attributes
      .attributeAs[A](name, clz)

    @inline
    def attributeAs[A: ClassTag](name: Any): A = self
      .attributes
      .attributeAs[A](name)

    @inline
    def optAttribute(name: Any): Option[INode] = self
      .attributes
      .optAttribute(name)

    @inline
    def optAttributeAs[A](name: Any, clz: Class[A]): Option[A] = self
      .attributes
      .optAttributeAs[A](name, clz)

    @inline
    def optAttributeAs[A: ClassTag](name: Any): Option[A] =
      optAttributeAs[A](name, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    @inline
    def replaceAttributes(attributes: INodeMap): INode =
      Node(self.optValue, self.children, attributes)

    @inline
    def setAttribute(attribute: (Any, INode)): INode =
      replaceAttributes(self.attributes + attribute)

    @inline
    def setAttribute(name: Any, attribute: INode): INode =
      replaceAttributes(self.attributes + (name, attribute))

    @inline
    def setAttribute(name: Any, value: Any): INode =
      setAttribute(name, Node(value))

    @inline
    def replaceAttribute(name: Any, mapper: INode => INode): INode =
      setAttribute(name, mapper(self.attribute(name)))

    @inline
    def addAttributes(attributes: IterableOnce[(Any, INode)]): INode =
      replaceAttributes(self.attributes ++ attributes)

    @inline
    def merge(other: INode): INode = Node(
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
  def mergeOptions(lhs: Option[_], rhs: Option[_]): Option[_] =
    lhs.map(v1 =>
      rhs.map(v2 =>
        mergeValues(v1, v2)
      ).getOrElse(v1) // return lhs unchanged (rhs is empty)
    ).orElse(rhs) // return rhs unchanged (lhs is empty)

  /**
   * @param lhs left-hand-side (first value)
   * @param rhs right-hand-side (second value)
   */
  def mergeValues(lhs: Any, rhs: Any): Any = {
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
}

trait INode {
  def optValue: Option[_]

  def children: INodeList

  def attributes: INodeMap

  def equals(other: INode): Boolean

  override def equals(obj: Any): Boolean =
    obj match {
      case other: INode =>
        equals(other)
      case _ =>
        false
    }
}
