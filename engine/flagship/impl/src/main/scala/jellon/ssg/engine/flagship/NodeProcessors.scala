package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.INodeProcessors
import jellon.ssg.engine.flagship.processors.{DefineNodeProcessor, LoopNodeProcessor, RootNodeProcessor, ScopeNodeProcessor, St4NodeProcessor, UnzipNodeProcessor, VelocityNodeProcessor}
import jellon.ssg.engine.flagship.spi.INodeProcessor

class NodeProcessors(delegates: Seq[INodeProcessor]) extends INodeProcessors {
  // try to order by how often they get used will shave nanoseconds off processing time ;)
  def this() = this(Seq(
    DefineNodeProcessor,
    ScopeNodeProcessor,
    LoopNodeProcessor,
    St4NodeProcessor,
    VelocityNodeProcessor,
    UnzipNodeProcessor,
    RootNodeProcessor,
  ))

  override def apply(): Seq[INodeProcessor] = delegates
}
