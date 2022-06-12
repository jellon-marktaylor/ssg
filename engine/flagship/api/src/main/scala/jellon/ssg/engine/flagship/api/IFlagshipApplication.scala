package jellon.ssg.engine.flagship.api

import jellon.ssg.engine.flagship.spi.INodeProcessor.{INPUT, INSTRUCTIONS}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.NodeMap

object IFlagshipApplication {
  val BASE_KEY: String = ""

  implicit class IFlagshipApplicationExt(self: IFlagshipApplication) {
    /**
     * Simply delegate to the engine
     *
     * @see [[jellon.ssg.engine.flagship.api.IFlagshipEngine#process(jellon.ssg.node.api.INodeMap, java.lang.Object, jellon.ssg.node.api.INode)]]
     */
    @inline
    def process(state: INodeMap, key: Any, node: INode): Unit =
      self.createEngine.process(state, key, node)

    @inline
    def processInstructions(instructions: INode): Unit =
      process(
        new NodeMap(Map[Any, INode](INSTRUCTIONS -> instructions)),
        BASE_KEY,
        instructions
      )

    @inline
    def processInstructionsWithInput(instructions: INode, input: INode): Unit = process(
      new NodeMap(Map[Any, INode](
        INSTRUCTIONS -> instructions,
        INPUT -> input,
      )),
      BASE_KEY,
      instructions
    )
  }

}

/**
 * The source for a [[jellon.ssg.engine.flagship.api.IFlagshipEngine]] which kicks off an SSG instance
 *
 * @see [[jellon.ssg.engine.flagship.api.IFlagshipApplication.IFlagshipApplicationExt#process(jellon.ssg.node.api.INodeMap, java.lang.Object, jellon.ssg.node.api.INode)]]
 * @see [[jellon.ssg.engine.flagship.api.IFlagshipApplication.IFlagshipApplicationExt#processInstructions(jellon.ssg.node.api.INode)]]
 * @see [[jellon.ssg.engine.flagship.api.IFlagshipApplication.IFlagshipApplicationExt#processInstructionsWithInput(jellon.ssg.node.api.INode, jellon.ssg.node.api.INode)]]
 */
trait IFlagshipApplication {
  def createEngine: IFlagshipEngine
}
