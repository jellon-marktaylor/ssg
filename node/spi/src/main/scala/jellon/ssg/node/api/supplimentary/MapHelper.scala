package jellon.ssg.node.api.supplimentary

import jellon.ssg.node.api.INode

import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.MapHasAsJava

object MapHelper {
  def toNodeMap(map: collection.Map[_, _]): Map[AnyRef, INode] = map
    .view
    .map(kv =>
      kv._1.asInstanceOf[AnyRef] -> INode(kv._2)
    )
    .to(ListMap)

  def asJava(map: Map[AnyRef, INode]): java.util.Map[Object, Object] = map
    .view
    .map(kv =>
      (kv._1.asInstanceOf[AnyRef], kv._2.asJava)
    )
    .to(ListMap)
    .asJava
}
