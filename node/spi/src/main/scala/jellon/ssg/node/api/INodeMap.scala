package jellon.ssg.node.api

import jellon.ssg.node.spi.{Node, NodeMap}

import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

object INodeMap {
  val empty: INodeMap = EmptyNodeMap

  // TODO: expose some methods on INodeMap (and INode, and INodeList) to allow some implementations to be mutable. this should delegate to those methods
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

    /**
     * either set or update a value in this [[INodeMap]]. If the value at key already exists, than the mergeFunction
     * will be invoked with the current value as the first parameter and the 'value' parameter as it's second parameter.
     * The default merge function is <code>_ ++ _</code>
     *
     * @param key           attribute name to update
     * @param value         used to insert or update the existing value
     * @param mergeFunction invoked to merge with the existing node, if applicable; see description above
     * @return a new INodeMap with value or mergedValue
     */
    @inline
    def mergeAttribute(key: Any, value: INode, mergeFunction: (INode, INode) => INode = _.merge(_)): INodeMap = self
      .optAttribute(key)
      .map( // do update
        current => {
          val merged = mergeFunction(current, value)
          self.setAttribute(key, merged)
        }
      )
      .getOrElse { // do insert
        self.setAttribute(key, value)
      }

    @inline
    def mergePathAttribute(path: String, value: INode, mergeFunction: (INode, INode) => INode = _.merge(_)): INodeMap =
      mergeAttributes(path.split('.'), value, mergeFunction)

    // TODO: this could greatly benefit from mutable INode implementations
    @inline
    def mergeAttributes(keys: Seq[_], value: INode, mergeFunction: (INode, INode) => INode = _.merge(_)): INodeMap = {
      // let self be represented by the following JSON:
      // { "a": { "b": { "foo": "bar" } } }
      // let keys be ["a", "b", "c", "d"]
      // result:
      // [
      //   ("a", { "a": { "b": { "foo": "bar" } } }), // ("a", self)
      //   ("b", { "b": { "foo": "bar" } } }), // ("b", self.attribute("a"))
      //   ("c", empty)
      //   ("d", empty)
      // ]
      def zip: Seq[(Any, INodeMap)] = {
        val init: (Any, INodeMap) =
          (keys.head, self)
        keys
          .tail
          .foldLeft(Seq(init))(
            (acc, key) => {
              val head = acc.head
              val parent: INodeMap = head._2
              val child: INode = parent.attribute(head._1)
              acc :+ (key, child.attributes)
            }
          )
      }

      if (keys.isEmpty) {
        throw new IllegalArgumentException(s"cannot insert node without at least 1 key: $value")
      } else if (keys.tail.isEmpty) {
        mergeAttribute(keys.head, value, mergeFunction)
      } else {
        val pathNodes: Seq[(Any, INodeMap)] = zip
          .reverse

        // continuing the example from the zip doc:
        // [
        //   ("d", empty) // head
        //   ("c", empty)
        //   ("b", { "b": { "foo": "bar" } } }), // ("b", self.attribute("a"))
        //   ("a", { "a": { "b": { "foo": "bar" } } }), // ("a", self)
        // ]
        //
        // Going to provide a lot of redundant information here
        // So what would be executed would be:
        // head = ("d", empty)
        // mergedNodeMap = { "d": value }
        //
        // 1: childMap = { "d": value }
        // key = "c"
        // parent = empty
        // child = empty <= empty("c")
        // merged = { "d": value } <= empty.addAttributes({ "d": value })
        //
        // 2: childMap = { "c": { "d": value } }
        // key = "b"
        // parent = { "b": { "foo": "bar" } }
        // child = { "foo": "bar" }
        // merged = { "foo": "bar", "c": { "d": value } } <= { "foo": "bar" }.addAttributes({ "c": { "d": value } })
        //
        // 3: childMap = { "b": { "foo": "bar", "c": { "d": value } } }
        // key = "a"
        // parent = { "a": { "b": { "foo": "bar" } } }
        // child = { "b": { "foo": "bar" } }
        // merged = { "b": { "foo": "bar", "c": { "d": value } } } <= { "b": { "foo": "bar" } }.addAttributes({ "b": { "foo": "bar", "c": { "d": value } } })
        //
        // result => { "a": { "b": { "foo": "bar", "c": { "d": value } } } }
        val head = pathNodes.head
        val mergedNodeMap = head._2.mergeAttribute(head._1, value, mergeFunction)
        pathNodes.tail.foldLeft[INodeMap](mergedNodeMap)(
          (childMap, kp) => {
            val key: Any = kp._1
            val parent: INodeMap = kp._2
            val child: INode = parent(key)
            val merged: INode = child.addAttributes(childMap.toMap)
            parent.setAttribute(key, merged)
          }
        )
      }
    }

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
trait INodeMap extends IAttributeNode {
  def apply(key: Any): INode = optAttribute(key)
    .getOrElse(INode.empty)

  def isEmpty: Boolean

  def nonEmpty: Boolean

  ////////////
  // ACCESSORS
  ////////////

  override def attributes: INodeMap = this

  override def hasAttributes: Boolean = nonEmpty

  ////////////
  // MUTATORS
  ////////////

  override def replaceAttributes(attributes: INodeMap): INodeMap =
    this ++ attributes
}
