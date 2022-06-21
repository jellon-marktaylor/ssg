package jellon.ssg.engine.flagship.modules

import jellon.ssg.engine.flagship.api.INodeProcessors
import jellon.ssg.engine.flagship.{AbstractGuiceModule, FlagshipNodeProcessors}

object ProcessorsModule extends AbstractGuiceModule {
  override def configure(): Unit = {
    bindTo[INodeProcessors](() => FlagshipNodeProcessors)
  }
}
