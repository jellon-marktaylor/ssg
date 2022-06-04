package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.INodeProcessors
import jellon.ssg.engine.flagship.spi.INodeProcessor

class NodeProcessors(delegates: Seq[INodeProcessor]) extends INodeProcessors {
  override def apply(): Seq[INodeProcessor] = delegates
}
