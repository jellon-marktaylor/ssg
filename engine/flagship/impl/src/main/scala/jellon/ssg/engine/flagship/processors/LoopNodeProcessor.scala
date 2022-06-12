package jellon.ssg.engine.flagship.processors

import grizzled.slf4j.Logging
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{AbstractNodeProcessor, INodeProcessor}
import jellon.ssg.engine.flagship.spi.INodeProcessor._
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node

object LoopNodeProcessor extends AbstractNodeProcessor("loop") with Logging {
  override def propagateOutput: Boolean =
    false

  override def output(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INode = super.output(state, key, node, engine)

  override def process(state: INodeMap, key: Any, loopNode: INode, engine: IFlagshipEngine): Unit = {
    val nameOfLoopNode: String = loopNode.attributeAs[String]("foreach")
    val loopOverNode: INode = engine.resolveNode(state, nameOfLoopNode)

    val result = loopOverNode
      .children
      .toSeq // Seq[INode]
      .zipWithIndex // Seq[(INode, Int)]
      .map(_.swap) // Seq[(Int, INode)]
      .foldLeft(INodeMap.empty)((r, kv) => {
        subprocess(state ++ r, loopNode, engine, kv)
        r
      })

    loopOverNode
      .attributes
      .toMap // Map[Any, INode]
      .foldLeft(result)((r, kv) => {
        subprocess(state ++ r, loopNode, engine, kv)
        r
      })
  }

  @inline
  private[this] def subprocess(state: INodeMap, loopNode: INode, engine: IFlagshipEngine, kv: (Any, INode)): Unit = {
    val (key, value) = kv
    val newState = this.newState(state, loopNode, key, value)
    logger.debug(s"$this =>\n(key, value) = ($key, $value)\nstate: $state\noutput: $newState")
    ScopeNodeProcessor.process(newState, "scope", loopNode.attribute("do"), engine)
  }

  @inline
  private[this] def newState(state: INodeMap, loopNode: INode, anyKey: Any, nodeValue: INode): INodeMap = state
    .setAttribute("foreach", foreachNode(anyKey, nodeValue))

  @inline
  private[this] def foreachNode(anyKey: Any, nodeValue: INode): INode =
    Node(Map[Any, Any](
      "key" -> anyKey,
      "value" -> nodeValue
    ))
}
