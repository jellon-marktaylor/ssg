package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipTestEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.engine.flagship.spi.INodeProcessor.instructionsNodeMap
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object ScopeNodeProcessorTests {
  var testResults: Vector[String] = Vector.empty

  val instructions: INode = Node(Map[Any, Any](
    "scope" -> Map(
      "define" -> Map(
        "action" -> Map(
          "foo" -> "bar"
        )
      ),
      "print" -> Seq(
        "foo"
      )
    )
  ))

  object PrintNodeProcessor extends AbstractNodeProcessor("print") {
    override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, print: INode): Unit =
      print
        .children
        .toSeq
        .map(_.valueAs[String])
        .foreach(value =>
          testResults = testResults :+ s"$value -> ${state("action").attributeAs[String](value)}"
        )
  }

}

class ScopeNodeProcessorTests extends AnyFunSpec {

  import ScopeNodeProcessorTests._

  describe("test returned INodeMap") {

    val engine: IFlagshipEngine = new FlagshipTestEngine(
      Seq(ScopeNodeProcessor, DefineNodeProcessor, PrintNodeProcessor)
    )

    it("should integrate define and print actions") {
      engine.process(instructionsNodeMap(instructions), "scope", instructions.attribute("scope"))
      assert(testResults.contains("foo -> bar"))
    }
  }
}
