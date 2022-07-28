package jellon.ssg.engine.flagship.processors

import grizzled.slf4j.Logging
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node

/**
 * Loops over objects in a list or map passing them into a scope. The state passed into the scope will define
 * "foreach.key" and "foreach.value" where key is either the index or name and value is the node being iterated over.
 * <br/>Name: loop
 *
 * @note foreach (required) points at any list or map value in the state
 * @note do (required) same as [[ScopeNodeProcessor]]
 * @example { "define": { "foo": "bar", "bar": "bat" }, "loop": { "foreach": "define", "do": { ... } } }
 * @example don't forget to check the tests for examples
 */
object LoopNodeProcessor extends AbstractNodeProcessor("loop") with Logging {
  override def propagateOutput: Boolean =
    false

  override def output(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INode =
    super.output(engine, state, key, node)

  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, loopNode: INode): Unit = {
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
    ScopeNodeProcessor.process(engine, newState, "scope", loopNode.attribute("do"))
  }

  @inline
  private[this] def newState(state: INodeMap, loopNode: INode, anyKey: Any, nodeValue: INode): INodeMap = state
    .setAttribute("foreach", foreachNode(anyKey, nodeValue))

  @inline
  private[this] def foreachNode(anyKey: Any, nodeValue: INode): INode =
    INode(Map[Any, Any](
      "key" -> anyKey,
      "value" -> nodeValue
    ))
}
