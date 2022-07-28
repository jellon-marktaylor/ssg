package jellon.ssg.node.api.supplimentary

import jellon.ssg.node.api.INode

object EmptyNode extends INode {
  override def asJava: AnyRef = null

  override def equals(other: INode): Boolean =
    this.eq(other)

  override def toString = "{}"
}
