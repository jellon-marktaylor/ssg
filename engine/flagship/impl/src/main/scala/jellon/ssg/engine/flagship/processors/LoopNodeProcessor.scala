package jellon.ssg.engine.flagship.processors

import grizzled.slf4j.Logging
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.INSTRUCTIONS
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node

object LoopNodeProcessor extends AbstractNodeProcessor("loop") with Logging {
  override def processAttributes(loopNode: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
    val nameOfLoopNode: String = loopNode.string("foreach")
    val loopOverNode: INode = engine.resolveNode(state, nameOfLoopNode)

    val result = loopOverNode
      .children
      .toSeq // Seq[INode]
      .zipWithIndex // Seq[(INode, Int)]
      .map(_.swap) // Seq[(Int, INode)]
      .foldLeft(INodeMap.empty)((r, kv) =>
        r ++ subprocess(state ++ r, engine, kv)
      )

    loopOverNode
      .attributes
      .toMap // Map[Any, INode]
      .foldLeft(result)((r, kv) =>
        r ++ subprocess(state ++ r, engine, kv)
      )
  }

  @inline
  private[this] def subprocess(state: INodeMap, engine: IFlagshipEngine, kv: (Any, INode)): INodeMap = {
    val newState = this.newState(state, kv._1, kv._2)
    logger.debug(s"$this => \ninput: $state\nscope: $newState")
    ScopeNodeProcessor.process(newState, engine)
  }

  @inline
  private[this] def newState(state: INodeMap, anyKey: Any, nodeValue: INode): INodeMap = state
    .replaceAttribute(INSTRUCTIONS, _.attribute("do"))
    .setAttribute("foreach", foreachNode(anyKey, nodeValue))

  @inline
  private[this] def foreachNode(anyKey: Any, nodeValue: INode): INode =
    Node(Map[Any, Any](
      "key" -> anyKey,
      "value" -> nodeValue
    ))
}
