package jellon.ssg.node.api

import scala.reflect.ClassTag

trait IParentNode[Self <: IParentNode[Self]] {
  def children: INodeList

  def toSeq: Seq[INode]

  def optIndex(index: Int): Option[INode] = this
    .toSeq
    .unapply(index)

  def optIndexAs[B](index: Int, clz: Class[B]): Option[B] = this
    .optIndex(index)
    .filter(clz.isInstance)
    .map(clz.cast)

  def optIndexAs[B: ClassTag](index: Int): Option[B] = this
    .optIndexAs(index, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def index(index: Int): INode = this
    .optIndex(index)
    .getOrElse(INode.empty)

  @throws[IndexOutOfBoundsException]
  @throws[ClassCastException]
  def indexAs[B <: AnyRef](index: Int, clz: Class[B]): B = this
    .optIndex(index)
    .map(value =>
      if (clz.isInstance(value))
        clz.cast(value)
      else
        throw new ClassCastException(s"$this[$index] Expected ${clz.getName}, but found ${if (value == null) "null" else value.getClass.getName}")
    )
    .getOrElse {
      throw new IndexOutOfBoundsException(s"$this[$index]")
    }

  @throws[IndexOutOfBoundsException]
  @throws[ClassCastException]
  def indexAs[B <: AnyRef : ClassTag](index: Int): B = this
    .indexAs(index, implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def addChild(child: INode): Self = this
    .setChildren(toSeq.appended(child))

  def setChildren(children: INodeList): Self

  def setChildren(children: Seq[INode]): Self = this
    .setChildren(INodeList(children))

  def addChildren(children: IterableOnce[INode]): Self = this
    .setChildren(toSeq ++ children)
}
