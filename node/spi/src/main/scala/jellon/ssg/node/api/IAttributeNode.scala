package jellon.ssg.node.api

import jellon.ssg.node.spi.Node

import scala.reflect.ClassTag

trait IAttributeNode {
  ////////////
  // ACCESSORS
  ////////////

  def attributes: INodeMap

  def hasAttributes: Boolean

  def keySet: Set[Any]

  def optAttribute(name: Any): Option[INode]

  def attribute(key: Any): INode = optAttribute(key).getOrElse(INode.empty)

  def attributeAs[B](name: Any, clz: Class[B]): B = optAttribute(name)
    .flatMap(_.optValue)
    .map {
      case value@b if clz.isInstance(value) =>
        clz.cast(b)
      case value =>
        throw new IllegalArgumentException(s"Expected type: ${clz.getName}, but found ${value.getClass.getName} in $this")
    }
    .getOrElse {
      throw new IllegalArgumentException(s"Expected type: ${clz.getName}, but found empty in $this")
    }

  def attributeAs[B: ClassTag](name: Any): B =
    attributeAs[B](name, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def optAttributeAs[B](name: Any, clz: Class[B]): Option[B] =
    optAttribute(name)
      .filter(clz.isInstance)
      .map(clz.cast)

  def optAttributeAs[B: ClassTag](name: Any): Option[B] =
    optAttributeAs[B](name, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  ////////////
  // MUTATORS
  ////////////

  def replaceAttributes(attributes: INodeMap): this.type

  def setAttribute(attribute: (Any, INode)): this.type =
    replaceAttributes(attributes + attribute)

  def setAttribute(key: Any, attribute: INode): this.type =
    replaceAttributes(attributes + (key -> attribute))

  def setAttribute(key: Any, value: Any): this.type = value match {
    case node: INode => setAttribute(key, node)
    case _ => setAttribute(key, Node(value))
  }

  def replaceAttribute(key: Any, value: INode, mapper: (INode, INode) => INode = _.merge(_)): this.type = optAttribute(key)
    .map(
      current => {
        val newValue = mapper(current, value)
        setAttribute(key, newValue)
      }
    )
    .getOrElse {
      setAttribute(key, value)
    }

  def replaceAttribute(key: Any, mapper: INode => INode): this.type = {
    val current = attribute(key)
    val newValue = mapper(current)
    setAttribute(key, newValue)
  }

  def addAttributes(attributes: IterableOnce[(Any, INode)]): this.type = attributes
    .iterator
    .foldLeft(this)(
      (result, kv) => result.replaceAttribute(kv._1, kv._2)
    )
}
