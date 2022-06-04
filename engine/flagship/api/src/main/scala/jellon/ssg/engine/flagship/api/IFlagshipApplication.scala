package jellon.ssg.engine.flagship.api

import jellon.ssg.engine.flagship.api.IFlagshipEngine.{INPUT, INSTRUCTIONS}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.NodeMap

object IFlagshipApplication {

  implicit class FlagshipApplicationExt(self: IFlagshipApplication) {
    @inline
    def process(initialState: Map[Any, INode]): Unit = self
      .process(new NodeMap(initialState))

    @inline
    def process(input: INode): Unit = process(Map[Any, INode](
      INPUT -> input
    ))

    @inline
    def process(input: INode, instructions: INode): Unit = process(Map[Any, INode](
      INPUT -> input,
      INSTRUCTIONS -> instructions
    ))
  }

}

trait IFlagshipApplication {
  def process(state: INodeMap): Unit
}
