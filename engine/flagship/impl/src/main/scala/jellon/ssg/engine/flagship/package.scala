package jellon.ssg.engine

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.INodeProcessor
import jellon.ssg.node.api.INodeMap

/**
 * [[FlagshipApplication]]
 *
 * @see jellon.ssg.engine.flagship.NodeProcessorHandler
 */
package object flagship {
  def executeProcessors(processors: Map[String, Seq[INodeProcessor]], path: String, state: INodeMap, engine: IFlagshipEngine): INodeMap =
    processors
      .getOrElse(path, Seq.empty)
      .foldLeft(INodeMap.empty)((r, processor) =>
        r ++ processor.process(state ++ r, engine)
      )
}
