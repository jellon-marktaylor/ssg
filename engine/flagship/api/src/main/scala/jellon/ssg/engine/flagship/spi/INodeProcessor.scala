package jellon.ssg.engine.flagship.spi

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.node.api.{INode, INodeList, INodeMap}
import jellon.ssg.node.spi.NodeMap

import java.io.IOException

object INodeProcessor {

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
   * @param engine reference providing the Flagship API for handling instructions
   * @param state  should contain keys such as those found in [[jellon.ssg.engine.flagship.spi.INodeProcessor]]
   * @param key    to the node. This is usually a string (from json, yaml, xml, etc) representing which instruction to execute. It is left as "Any" to make the framework flexible to customization.
   */
  @throws[IOException]
  def process(engine: IFlagshipEngine, state: INodeMap, key: Any, node: INode): INodeMap
}
