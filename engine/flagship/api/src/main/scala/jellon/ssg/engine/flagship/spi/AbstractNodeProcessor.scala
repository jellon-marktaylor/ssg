package jellon.ssg.engine.flagship.spi

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.node.api.{INode, INodeList, INodeMap}

import java.io.IOException

class AbstractNodeProcessor(val name: String) extends INodeProcessor {
  /**
   * A typical use-case is to return true if key equals a particular string.
   *
   * @param key  to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node the node this processor might process
   * @return true if output and process should be called for this key and node
   */
  def handles(key: Any, node: INode): Boolean =
    key == name

  /**
   * @return true if [[jellon.ssg.engine.flagship.spi.INodeProcessor.INodeProcessorNodeMapExtensions]] should be added to the current state for processors after this one
   */
  def propagateOutput: Boolean =
    false

  /**
   * @param engine reference providing the Flagship API for handling instructions
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node   the node this processor is processing
   * @return parameters to pass to [[jellon.ssg.engine.flagship.spi.INodeProcessor#process(jellon.ssg.node.api.INodeMap, jellon.ssg.node.api.INodeMap, jellon.ssg.engine.flagship.api.IFlagshipEngine) process(INodeMap, INodeMap, IFlagshipEngine]]
   */
  def output(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INode =
    node

  /**
   * @param engine reference providing the Flagship API for handling instructions
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node   the node this processor is processing
   */
  @throws[IOException]
  def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): Unit = {}

  /**
   * This is probably the most important method in the Flagship engine. It is called directly or indirectly by the application, engine, and any node (instruction) processor
   * <br/>If [[jellon.ssg.engine.flagship.spi.INodeProcessor#handles(java.lang.Object, jellon.ssg.node.api.INode)]] returns
   * true, then run [[jellon.ssg.engine.flagship.spi.INodeProcessor#process(jellon.ssg.node.api.INodeMap, jellon.ssg.node.api.INode, jellon.ssg.engine.flagship.api.IFlagshipEngine)]]
   *
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node   the node this processor is processing
   * @param engine reference providing the Flagship API for handling instructions
   * @return unless [[jellon.ssg.engine.flagship.spi.INodeProcessor#propagateOutput()]] returns true, this will return the 'state' parameter, otherwise 'state ++ output'
   * @see [[jellon.ssg.engine.flagship.spi.INodeProcessor#output(jellon.ssg.node.api.INodeMap, java.lang.Object, jellon.ssg.node.api.INode)]]
   */
  @throws[IOException]
  final def process(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INodeMap = {
    if (handles(key, node)) {
      val output: INode = this.output(engine, state, key, node)
      execute(engine, state, key, output)

      if (propagateOutput) {
        state ++ output.attributes
      } else {
        state
      }
    } else {
      state
    }
  }

  def processEachChild(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INodeList): INodeMap =
    if (node.isEmpty)
      state
    else
      node.toSeq.foldLeft[INodeMap](state)((acc, child) => process(engine, acc, key, child))

  def processEachChild(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INodeMap =
    if (node.hasChildren)
      processEachChild(engine, state, key, node.children)
    else
      state
}
