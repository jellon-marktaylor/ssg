package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{AbstractNodeProcessor, INodeProcessor}
import jellon.ssg.node.api.{INode, INodeMap}

object ScopeNodeProcessor extends AbstractNodeProcessor("scope") {
  override def propagateOutput: Boolean =
    false

  override def output(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INode =
    super.output(state, key, node, engine)

  override def process(state: INodeMap, key: Any, scopeNode: INode, engine: IFlagshipEngine): Unit = {
    this.processEachChild(state, key, scopeNode.children, engine)

    scopeNode
      .attributes
      .toMap
      .foldLeft[INodeMap](INodeMap.empty)((r, kv) => {
        val (childKey, child) = kv
        r ++ engine.process(state ++ r, childKey, child)
      })
  }
}
