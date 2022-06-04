package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.instructionsNodeMap
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INodeList, INodeMap}
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

class ScopeNodeProcessorTests extends AnyFunSpec {
  var output: Vector[String] = Vector.empty

  val instructions: INodeMap = instructionsNodeMap(Node(Map[Any, Any](
    "define" -> Map(
      "action" -> Map(
        "foo" -> "bar"
      )
    ),
    "print" -> Seq(
      "foo"
    )
  )))

  describe("test returned INodeMap") {

    val engine: IFlagshipEngine = new FlagshipEngine(Map(
      "scope" -> Seq(ScopeNodeProcessor),
      "define" -> Seq(DefineNodeProcessor),
      "print" -> Seq(PrintNodeProcessor),
    ), null, null)

    engine.process("scope", instructions)

    it("should integrate define and print actions") {
      assert(output.contains("foo -> bar"))
    }
  }

  object PrintNodeProcessor extends AbstractNodeProcessor("print") {
    override def processChildren(print: INodeList, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
      print.toSeq
        .map(_.valueAs[String])
        .foreach(value =>
          output = output :+ s"$value -> ${state("action").attributeAs[String](value)}"
        )

      INodeMap.empty
    }

    override def processAttributes(instructions: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      throw new UnsupportedOperationException()
  }

}
