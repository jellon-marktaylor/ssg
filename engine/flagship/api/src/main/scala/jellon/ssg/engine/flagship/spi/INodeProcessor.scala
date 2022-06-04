package jellon.ssg.engine.flagship.spi

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.node.api.INodeMap

/** [[NodeProcessorHandler]]
 *
 * @see jellon.ssg.engine.flagship.NodeProcessorHandler
 */
trait INodeProcessor {
  val path: String

  def process(state: INodeMap, engine: IFlagshipEngine): INodeMap
}
