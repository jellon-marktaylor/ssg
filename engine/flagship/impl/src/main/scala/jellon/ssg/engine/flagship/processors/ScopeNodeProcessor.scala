package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.INodeMap

object ScopeNodeProcessor extends AbstractNodeProcessor("scope") {
  override def processAttributes(scopeNode: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
    val result = scopeNode.optAttribute("define")
      .map(defineNode =>
        engine.processInstructions("define", state, defineNode)
      )
      .getOrElse(INodeMap.empty)

    scopeNode
      .keySet
      .filter(_.isInstanceOf[String])
      .map(_.asInstanceOf[String])
      .filterNot(_ == "define")
      .foldLeft(result)((r, key) =>
        r ++ engine.processInstructions(key, state ++ r, scopeNode(key))
      )
  }
}
