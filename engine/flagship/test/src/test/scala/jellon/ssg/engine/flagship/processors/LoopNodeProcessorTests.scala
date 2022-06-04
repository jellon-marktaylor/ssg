package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{IFlagshipNodeMapExtensions, INPUT, INSTRUCTIONS}
import jellon.ssg.engine.flagship.st4.ST4ResolverFactory
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

class LoopNodeProcessorTests extends AnyFunSpec {
  val input: INode = Node(Map[Any, Any](
    "list" -> Seq(
      "li1",
      "li2",
    ),
    "map" -> Map(
      "key1" -> "value1",
      "key2" -> "value2",
    )
  ))

  describe("LoopNodeProcessor") {
    it("should process each node in a NodeList") {
      val output = new PrintNodeProcessor
      val engine: IFlagshipEngine = new FlagshipEngine(Map(
        "loop" -> Seq(LoopNodeProcessor),
        "print" -> Seq(output),
      ), ST4ResolverFactory, null)

      engine.process("loop", Node(Map[Any, Any](
        INPUT -> input,
        INSTRUCTIONS -> Map(
          "foreach" -> "input.list",
          "do" -> Map(
            "print" -> "foreach.value"
          )
        )
      )))

      assert(output.output.size === 2)
      assert(output.output.contains("li1"))
      assert(output.output.contains("li2"))
    }

    it("should process each node in a NodeMap") {
      val output = new PrintNodeProcessor
      val engine: IFlagshipEngine = new FlagshipEngine(Map(
        "loop" -> Seq(LoopNodeProcessor),
        "print" -> Seq(output),
      ), ST4ResolverFactory, null)

      engine.process("loop", Node(Map[Any, Any](
        INPUT -> input,
        INSTRUCTIONS -> Map(
          "foreach" -> "input.map",
          "do" -> Map(
            "print" -> "foreach.value"
          )
        )
      )))

      assert(output.output.size === 2)
      assert(output.output.contains("value1"))
      assert(output.output.contains("value2"))
    }
  }

  class PrintNodeProcessor extends INodeProcessor {
    val path: String = "print"

    var output: Vector[String] = Vector.empty

    override def process(state: INodeMap, engine: IFlagshipEngine): INodeMap = {
      val raw: String = state.instructions.valueAs[String]
      val node = engine.resolveNode(state, raw)
      output = output :+ s"${node.valueAs[String]}"
      INodeMap.empty
    }
  }

}
