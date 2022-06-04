package jellon.ssg.engine.flagship.api

import jellon.ssg.engine.flagship.spi.INodeProcessor

trait INodeProcessors {
  def apply(): Seq[INodeProcessor]
}
