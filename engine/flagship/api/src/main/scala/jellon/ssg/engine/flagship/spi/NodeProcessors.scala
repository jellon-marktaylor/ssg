package jellon.ssg.engine.flagship.spi

import jellon.ssg.engine.flagship.api.INodeProcessors

class NodeProcessors(delegates: Seq[INodeProcessor]) extends INodeProcessors {
  override def apply(): Seq[INodeProcessor] = delegates
}
