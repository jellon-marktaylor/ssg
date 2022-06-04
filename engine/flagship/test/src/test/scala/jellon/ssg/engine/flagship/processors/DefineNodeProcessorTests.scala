package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.FlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{INSTRUCTIONS, instructionsNodeMap}
import jellon.ssg.engine.flagship.processors.DefineNodeProcessorTests.{engine, instructions}
import jellon.ssg.engine.flagship.st4.ST4ResolverFactory
import jellon.ssg.node.api.INodeMap
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object DefineNodeProcessorTests {
  val instructions: INodeMap = instructionsNodeMap(Node(Map[Any, Any](
    "simple" -> Map(
      "key1" -> "value1",
      "key2" -> "value2",
    ),
    "unresolved" -> Map(
      "key1" -> "<simple.key1>",
      "key2" -> "<simple.key2>",
      "key3" -> 5,
    )
  )))

  val engine: IFlagshipEngine = new FlagshipEngine(Map("define" -> Seq(DefineNodeProcessor)), ST4ResolverFactory, null)
}

class DefineNodeProcessorTests extends AnyFunSpec {
  describe("test simple INodeMap") {

    val actual = engine.process("define", instructions)

    it("should contain a simple value") {
      val simple = actual("simple").attributes
      assert(simple.string("key1") === "value1")
      assert(simple.string("key2") === "value2")
    }

    it("should not asText string values") {
      val unresolved = actual("unresolved").attributes
      assert(unresolved.string("key1") === "<simple.key1>")
      assert(unresolved.string("key2") === "<simple.key2>")
      assert(unresolved.attribute("key3").value === 5)
    }
  }

  describe("test copy nodes via * key") {
    val cloneValuesNode = Node(Map[Any, Any](
      "*" -> "unresolved"
    ))

    val updatedInstructions = instructions.replaceAttribute(
      INSTRUCTIONS,
      _.setAttribute("clone", cloneValuesNode)
    )

    val actual = engine.process("define", updatedInstructions)

    val clone = actual("clone").attributes

    it("should contain a copy of all the resolved values in the \"unresolved\" node") {
      assert(clone.string("key1") === "<simple.key1>")
      assert(clone.string("key2") === "<simple.key2>")
      assert(clone.attribute("key3").value === 5)
    }
  }
}
