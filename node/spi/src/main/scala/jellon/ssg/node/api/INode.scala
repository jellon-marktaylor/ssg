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
    def optValueAs[B](clz: Class[B]): Option[B] = self
      .optValue
      .filter(clz.isInstance)
      .map(clz.cast)

    @inline
    def optValueAs[B: ClassTag]: Option[B] =
      optValueAs(implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

    @inline
    @throws[NoSuchElementException]
    def value: Any = self.optValue match {
      case Some(value) => value
      case None => throw new NoSuchElementException(this + " is empty")
    }

    @inline
    def valueAs[B](clz: Class[B]): B = optValueAs[B](clz) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(this + " does not contain a value of type " + clz.getName)
    }

    @inline
    def valueAs[B: ClassTag]: B =
      valueAs(implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

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

    // MISC

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

trait INode extends IAttributeNode {
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

  ////////////
  // ACCESSORS
  ////////////

  def hasAttributes: Boolean = attributes
    .nonEmpty

  def keySet: Set[Any] = attributes
    .keySet

  def optAttribute(name: Any): Option[INode] = attributes
    .optAttribute(name)

  ////////////
  // MUTATORS
  ////////////

  def replaceAttributes(attributes: INodeMap): INode =
    Node(optValue, children, attributes)
}
