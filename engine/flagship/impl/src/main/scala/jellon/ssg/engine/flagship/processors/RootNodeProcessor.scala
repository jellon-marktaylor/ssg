package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipApplication.BASE_KEY
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{AbstractNodeProcessor, INodeProcessor}
import jellon.ssg.node.api.{INode, INodeMap}

object RootNodeProcessor extends RootNodeProcessor(ScopeNodeProcessor)

class RootNodeProcessor(delegate: INodeProcessor) extends AbstractNodeProcessor(BASE_KEY) {
  override def propagateOutput: Boolean =
    delegate.propagateOutput

  override def output(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INode =
    delegate.output(state, key, node, engine)

  override def process(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): Unit =
    delegate.process(state, key, node, engine)
}
