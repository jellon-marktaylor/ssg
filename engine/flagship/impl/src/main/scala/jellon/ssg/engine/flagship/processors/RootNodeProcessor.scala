package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.BASE_PATH
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.INodeMap

object RootNodeProcessor extends AbstractNodeProcessor(BASE_PATH) {
  override def processAttributes(instructions: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
    ScopeNodeProcessor.processAttributes(instructions, state, engine)
}
