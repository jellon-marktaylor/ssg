package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipTestApplication
import jellon.ssg.engine.flagship.velocity.PackageObjectTests._
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object VelocityNodeProcessorTests {
  val outputResource: String = "testOutput"

  val instructions: INode = Node(Map[Any, Any](
    "velocity" -> Map(
      "template" -> templateName,
      "output" -> outputResource,
      "using" -> "input"
    )
  ))
}

class VelocityNodeProcessorTests extends AnyFunSpec {

  import VelocityNodeProcessorTests._

  describe("process(state: INodeMap, key: Any, velocityNode: INode, engine: IFlagshipEngine)") {
    it("should integrate with the application engine to write the expected file") {
      val engine: FlagshipTestApplication = new FlagshipTestApplication()
      engine.processInstructionsWithInput(instructions, model)
      val actual = engine.lookupTestOutputOrEmpty(outputResource)
      assertResult(expectedOutput)(actual)
    }
  }
}
