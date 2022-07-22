package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipApplication.BASE_KEY
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INode, INodeMap}

object RootNodeProcessor extends AbstractNodeProcessor(BASE_KEY) {
  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): Unit =
    ScopeNodeProcessor.process(engine, state, "scope", node)
}
