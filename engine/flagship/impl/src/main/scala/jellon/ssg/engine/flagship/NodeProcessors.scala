package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.INodeProcessors
import jellon.ssg.engine.flagship.processors.{DefineNodeProcessor, LoopNodeProcessor, RootNodeProcessor, ScopeNodeProcessor, St4NodeProcessor, UnzipNodeProcessor}
import jellon.ssg.engine.flagship.spi.INodeProcessor

class NodeProcessors(delegates: Seq[INodeProcessor]) extends INodeProcessors {
  def this() = this(Seq(
    RootNodeProcessor,
    DefineNodeProcessor,
    LoopNodeProcessor,
    ScopeNodeProcessor,
    St4NodeProcessor,
    UnzipNodeProcessor
  ))

  override def apply(): Seq[INodeProcessor] = delegates
}
