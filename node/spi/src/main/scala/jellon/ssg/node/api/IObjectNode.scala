package jellon.ssg.node.api

import scala.reflect.ClassTag

trait IObjectNode[Self <: IObjectNode[Self]] {
  def attributes: INodeMap

  def toMap: Map[AnyRef, INode]

  def keySet: Set[AnyRef] = this
    .toMap
    .keySet

  ////////////
  // ACCESSORS
  ////////////

  def optAttribute(key: Any): Option[INode] = this
    .toMap
    .get(key.asInstanceOf[AnyRef])

  def optAttributeAs[B](key: Any, clz: Class[B]): Option[B] = this
    .optAttribute(key)
    .filter(clz.isInstance)
    .map(clz.cast)

  def optAttributeAs[B: ClassTag](key: Any): Option[B] = this
    .optAttributeAs[B](key, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def attribute(key: Any): INode = this
    .toMap
    .getOrElse(key.asInstanceOf[AnyRef], INode.empty)

  @throws[IllegalArgumentException]
  @throws[ClassCastException]
  def attributeAs[B](key: Any, clz: Class[B]): B = this
    .optAttribute(key)
    .flatMap(_.optValue)
    .map {
      case value@b if clz.isInstance(value) =>
        clz.cast(b)
      case value =>
        throw new IllegalArgumentException(s"$this[$key] Expected ${clz.getName}, but found ${if (value == null) "null" else value.getClass.getName}")
    }
    .getOrElse {
      throw new IllegalArgumentException(s"$this[$key]")
    }

  @throws[IllegalArgumentException]
  @throws[ClassCastException]
  def attributeAs[B: ClassTag](key: Any): B = this
    .attributeAs[B](key, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  ////////////
  // MUTATORS
  ////////////

  def setAttribute(attribute: (_, INode)): Self = this
    .setAttributes(attributes + attribute)

  def setAttribute(key: Any, attribute: INode): Self = this
    .setAttributes(attributes + (key -> attribute))

  def setAttribute(key: Any, value: Any): Self = value match {
    case node: INode => setAttribute(key, node)
    case _ => setAttribute(key, INode(value))
  }

  def setPathAttribute(keys: Seq[_], value: Any): Self = {
    val head = keys.head
    val tail = keys.tail
    if (tail.isEmpty) {
      setAttribute(head, value)
    } else {
      val temp = attribute(head)
      val child = temp
        .setPathAttribute(tail, value)
      setAttribute(head, child)
    }
  }

  def mergeAttribute(key: Any, mapper: INode => INode): Self = {
    val current = attribute(key)
    val newValue = mapper(current)
    setAttribute(key, newValue)
  }

  /**
   * either set or update a value in this [[INodeMap]]. If the value at key already exists, than the mergeFunction
   * will be invoked with the current value as the first parameter and the 'value' parameter as it's second parameter.
   * The default merge function is <code>_ ++ _</code>
   *
   * @param key    attribute name to update
   * @param value  used to insert or update the existing value
   * @param mapper invoked to merge with the existing node, if applicable; see description above
   * @return a new INodeMap with value or mergedValue
   */
  def mergeAttribute(key: Any, value: INode, mapper: (INode, INode) => INode = _.merge(_)): Self = {
    val current = optAttribute(key)
    val newValue = current
      .map(
        mapper(_, value)
      )
      .getOrElse(value)
    setAttribute(key, newValue)
  }

  def setAttributes(elements: INodeMap): Self

  def mergeAttributes(values: Seq[(_, INode)]): Self = this
    .mergeAttributes(
      values,
      (lhs: INode, rhs: INode) => IObjectNode.merge(lhs, rhs) // this looks like it could be simplified to just "IObjectNode.merge", but compiler doesn't take it
    )

  def mergeAttributes(values: Seq[(_, INode)], mergeFunction: (INode, INode) => INode): Self =
    values
      .foldLeft(this.asInstanceOf[Self])(
        (acc, kv) => acc.mergeAttribute(kv._1, kv._2, mergeFunction)
      )

  def addAttributes(values: IterableOnce[(_, INode)]): Self =
    values
      .iterator
      .foldLeft(this.asInstanceOf[Self])(
        (acc, kv) => acc.mergeAttribute(kv._1, kv._2)
      )

  def mergePathAttribute(path: String, value: INode): Self = this
    .mergePathAttribute(path, value, IObjectNode.merge)

  def mergePathAttribute(path: String, value: INode, mergeFunction: (INode, INode) => INode): Self = {
    val keys: Seq[AnyRef] = path.split("\\.").toSeq
    mergeAttributes(keys, value, mergeFunction)
  }

  def mergeAttributes(keys: Seq[_], value: INode): Self = this
    .mergeAttributes(keys, value, IObjectNode.merge)

  def mergeAttributes(keys: Seq[_], value: INode, mergeFunction: (INode, INode) => INode): Self =
    if (keys.isEmpty)
      this.asInstanceOf[Self]
    else {
      val head = keys.head
      val tail = keys.tail
      if (tail.isEmpty) {
        mergeAttribute(head, value, mergeFunction)
      } else {
        mergeAttribute(
          head,
          child =>
            child.mergeAttributes(tail, value, mergeFunction)
        )
      }
    }
}

object IObjectNode {
  def merge(lhs: INode, rhs: INode): INode = lhs.merge(rhs)
}