package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.processors.{DefineNodeProcessor, LoopNodeProcessor, RootNodeProcessor, ScopeNodeProcessor, St4NodeProcessor, UnzipNodeProcessor, VelocityNodeProcessor}
import jellon.ssg.engine.flagship.spi.NodeProcessors

// try to order by how often they get used will shave nanoseconds off processing time ;)
object FlagshipNodeProcessors extends NodeProcessors(Seq(
  DefineNodeProcessor,
  ScopeNodeProcessor,
  LoopNodeProcessor,
  St4NodeProcessor,
  VelocityNodeProcessor,
  UnzipNodeProcessor,
  RootNodeProcessor,
))
