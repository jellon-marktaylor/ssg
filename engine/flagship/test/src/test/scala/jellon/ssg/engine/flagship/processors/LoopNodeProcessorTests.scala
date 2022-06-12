package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.engine.flagship.spi.INodeProcessor.{INPUT, INSTRUCTIONS, INodeProcessorNodeMapExtensions}
import jellon.ssg.engine.flagship.st4.ST4ResolverFactory
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.{Node, NodeMap}
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

  def runTestWithInstructions(elements: Seq[(Any, Any)]): Vector[String] = {
    val output = new PrintNodeProcessor
    val engine: IFlagshipEngine = new FlagshipEngine(null, ST4ResolverFactory, Seq(LoopNodeProcessor, output))

    val instructions: INode = Node(Map(
      "loop" -> Map(elements: _*)
    ))

    val state: INodeMap = Node(Map[Any, Any](
      INPUT -> input,
      INSTRUCTIONS -> instructions
    )).attributes

    engine.process(state, "loop", instructions.attribute("loop"))

    output.output
  }

  describe("LoopNodeProcessor") {
    it("should process each node in a NodeList") {
      val output = runTestWithInstructions(Seq(
        "foreach" -> "input.list",
        "do" -> Map(
          "print" -> "foreach.value"
        )
      ))

      assert(output.size === 2)
      assert(output.contains("li1"))
      assert(output.contains("li2"))
    }

    it("should process each node in a NodeMap") {
      val output = runTestWithInstructions(Seq(
        "foreach" -> "input.map",
        "do" -> Map(
          "print" -> "foreach.value"
        )
      ))

      assert(output.size === 2)
      assert(output.contains("value1"))
      assert(output.contains("value2"))
    }
  }

  class PrintNodeProcessor extends AbstractNodeProcessor("print") {
    var output: Vector[String] = Vector.empty

    override def process(state: INodeMap, key: Any, printNode: INode, engine: IFlagshipEngine): Unit = {
      val raw: String = printNode.valueAs[String]
      val node = engine.resolveNode(state, raw)
      output = output :+ s"${node.valueAs[String]}"
    }
  }

}
