package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.processors.DefineNodeProcessorTests.{engine, instructions}
import jellon.ssg.engine.flagship.spi.INodeProcessor.instructionsNodeMap
import jellon.ssg.engine.flagship.{FlagshipEngine, ResolverFactory}
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object DefineNodeProcessorTests {
  val instructions: INode = Node(Map[Any, Any](
    "define" -> Map(
      "simple" -> Map(
        "key1" -> "value1",
        "key2" -> "value2",
      ),
      "unresolved" -> Map(
        "key1" -> "<simple.key1>",
        "key2" -> "<simple.key2>",
        "key3" -> 5,
      )
    )
  ))

  val engine: IFlagshipEngine = new FlagshipEngine(null, ResolverFactory, Seq(DefineNodeProcessor))
}

class DefineNodeProcessorTests extends AnyFunSpec {
  describe("test simple INodeMap") {

    lazy val actual = engine.process(instructionsNodeMap(instructions), "define", instructions.attribute("define"))

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
    lazy val cloneValuesNode = Node(Map[Any, Any](
      "*" -> "unresolved"
    ))

    lazy val updatedInstructions = instructions.replaceAttribute(
      "define",
      _.setAttribute("clone", cloneValuesNode)
    )

    lazy val actual = engine.process(instructionsNodeMap(updatedInstructions), "define", updatedInstructions.attribute("define"))

    lazy val clone = actual("clone").attributes

    it("should contain a copy of all the resolved values in the \"unresolved\" node") {
      clone
      assert(clone.string("key1") === "<simple.key1>")
      assert(clone.string("key2") === "<simple.key2>")
      assert(clone.attribute("key3").value === 5)
    }
  }
}
