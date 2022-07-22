package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INode, INodeMap}

/**
 * Use the FlagshipEngine to execute each nested node sequentially. Order matters when using instructions that influence
 * the state, such as a "define" node.
 */
object ScopeNodeProcessor extends AbstractNodeProcessor("scope") {
  override def propagateOutput: Boolean =
    false

  override def output(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INode =
    super.output(engine, state, key, node)

  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, scopeNode: INode): Unit = {
    processEachChild(engine, state, key, scopeNode.children)

    scopeNode
      .attributes
      .toMap
      .foldLeft[INodeMap](INodeMap.empty)((r, kv) => {
        val (childKey, child) = kv
        r ++ engine.process(state ++ r, childKey, child)
      })
  }
}
