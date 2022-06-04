package jellon.ssg.engine.flagship.spi

import grizzled.slf4j.Logging
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{IFlagshipNodeMapExtensions, INSTRUCTIONS}
import jellon.ssg.node.api.{INodeList, INodeMap}

/** [[NodeProcessorHandler]]
 *
 * @see jellon.ssg.engine.flagship.NodeProcessorHandler
 */
abstract class AbstractNodeProcessor(val path: String) extends INodeProcessor with Logging {
  override def process(state: INodeMap, engine: IFlagshipEngine): INodeMap = {
    val instructions = state.instructions

    var result: INodeMap = INodeMap.empty
    if (instructions.hasChildren) {
      result = processChildren(instructions.children, state, engine)

      if (instructions.hasAttributes) {
        result = processAttributes(instructions.attributes, state ++ result, engine)
      }
    } else if (instructions.hasAttributes) {
      result = processAttributes(instructions.attributes, state, engine)
    }

    logger.debug(s"$this => \ninput:  $state\noutput: $result")

    result
  }

  def processChildren(instructions: INodeList, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
    instructions.toSeq.foldLeft(INodeMap.empty)((result, element) =>
      result ++ process(state.replaceAttribute(INSTRUCTIONS, _ => element), engine)
    )
  }

  def processAttributes(instructions: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap
}
