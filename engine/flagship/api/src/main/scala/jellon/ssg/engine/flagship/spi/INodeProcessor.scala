package jellon.ssg.engine.flagship.spi

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.node.api.{INode, INodeList, INodeMap}
import jellon.ssg.node.spi.NodeMap

import java.io.IOException

object INodeProcessor {

  implicit class INodeProcessorExt(self: INodeProcessor) {
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
    def apply(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INodeMap = {
      if (self.handles(key, node)) {
        val output: INode = self.output(state, key, node, engine)
        self.process(state, key, output, engine)

        if (self.propagateOutput) {
          state ++ output.attributes
        } else {
          state
        }
      } else {
        state
      }
    }

    def processEachChild(state: INodeMap, key: Any, node: INodeList, engine: IFlagshipEngine): INodeMap =
      if (node.isEmpty)
        state
      else
        node.toSeq.foldLeft[INodeMap](state)((acc, child) => apply(acc, key, child, engine))

    def processEachChild(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INodeMap =
      if (node.hasChildren)
        processEachChild(state, key, node.children, engine)
      else
        state
  }

  val INSTRUCTIONS: String = "instructions"

  val INPUT: String = "input"

  val OUTPUT: String = "output"

  def instructionsNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](INSTRUCTIONS -> value))

  def inputNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](INPUT -> value))

  def outputNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](OUTPUT -> value))

  implicit class INodeProcessorNodeMapExtensions(self: INodeMap) {
    def instructions: INode = self(INSTRUCTIONS)

    def input: INode = self(INPUT)

    def output: INode = self(OUTPUT)
  }

}

/**
 * @see [[jellon.ssg.engine.flagship.api.IFlagshipEngine]]
 */
trait INodeProcessor {
  /**
   * A typical use-case is to return true if key equals a particular string.
   *
   * @param key  to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node the node this processor might process
   * @return true if output and process should be called for this key and node
   */
  def handles(key: Any, node: INode): Boolean

  /**
   * @return true if [[jellon.ssg.engine.flagship.spi.INodeProcessor.INodeProcessorNodeMapExtensions]] should be added to the current state for processors after this one
   */
  def propagateOutput: Boolean =
    false

  /**
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node   the node this processor is processing
   * @param engine reference providing the Flagship API for handling instructions
   * @return parameters to pass to [[jellon.ssg.engine.flagship.spi.INodeProcessor#process(jellon.ssg.node.api.INodeMap, jellon.ssg.node.api.INodeMap, jellon.ssg.engine.flagship.api.IFlagshipEngine) process(INodeMap, INodeMap, IFlagshipEngine]]
   */
  def output(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): INode =
    node

  /**
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   * @param node   the node this processor is processing
   * @param engine reference providing the Flagship API for handling instructions
   */
  @throws[IOException]
  def process(state: INodeMap, key: Any, node: INode, engine: IFlagshipEngine): Unit = {}
}
