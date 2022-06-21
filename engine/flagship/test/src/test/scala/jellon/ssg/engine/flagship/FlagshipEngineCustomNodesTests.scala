package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.IFlagshipApplication.BASE_KEY
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.INodeProcessor._
import jellon.ssg.engine.flagship.spi.{AbstractNodeProcessor, NodeProcessors}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ListMap

class FlagshipEngineCustomNodesTests extends AnyFunSpec {
  var testResults: Vector[String] = Vector.empty

  /** goal: add "hello -> world" and "foo -> bar" to testResults using custom node processors that demonstrate a variety of functionalities
   * {{{
   * {
   *   "foo": {
   *     "dictionary": {
   *       "a": 1
   *       "b", 3
   *     },
   *     "list": [ "world", "ignored", "bar" ],
   *     "bar": {
   *       "bat": {
   *         "hello": "a",
   *         "foo": "b"
   *       }
   *     }
   *   }
   * }
   * }}}
   *
   * for each value, map its key to `list[input(value)]` where input(value) returns an integer and list[index] returns a string
   * input("a") => 1, list[1] => "world"; therefore list[input("a")] => "world"; therefore "hello" -> "a" gets mapped to "hello -> world"
   * input("b") => 3, list[3] => "bar"; therefore list[input("b")] => "bar"; therefore "bar" -> "b" gets mapped to "foo -> bar"
   */
  describe("custom processors example") {
    // this is how easy it can be to define a node in code
    val instructions: INode = Node(ListMap(
      "foo" -> ListMap(
        "dictionary" -> ListMap(
          "a" -> 1,
          "b" -> 3
        ),
        "list" -> Vector("world", "ignored", "bar"),
        "bar" -> ListMap(
          "bat" -> ListMap(
            "hello" -> "a",
            "foo" -> "b"
          )
        )
      )
    ))

    val processors = new NodeProcessors(Seq(
      // call foo
      RootNodeProcessor,
      // use the merged result from calling dictionary and list to call bar
      FooNodeProcessor,
      // returns the dictionary node itself
      DictionaryNodeProcessor,
      // defines the input list as a node called "testResults"
      ListNodeProcessor,
      // calls the bat node
      BarNodeProcessor,
      // write to `testResults` Vector; see scaladoc on `description` above
      BatNodeProcessor
    ))

    new FlagshipTestApplication(processors)
      .processInstructions(instructions)

    it("should only produce 2 outputs") {
      assert(testResults.size == 2)
    }

    it("should contain hello -> world") {
      assert(testResults.contains("hello -> world"))
    }

    it("should contain foo -> bar") {
      assert(testResults.contains("foo -> bar"))
    }
  }

  object RootNodeProcessor extends AbstractNodeProcessor(BASE_KEY) {
    override def process(state: INodeMap, key: Any, rootNode: INode, engine: IFlagshipEngine): Unit =
      engine.process(state, "foo", rootNode.attribute("foo"))
  }

  object ListNodeProcessor extends AbstractNodeProcessor("list") {
    override def propagateOutput: Boolean =
      true

    override def output(state: INodeMap, key: Any, list: INode, engine: IFlagshipEngine): INode =
      Node(outputNodeMap(list))
  }

  object DictionaryNodeProcessor extends AbstractNodeProcessor("dictionary") {
    override def propagateOutput: Boolean =
      true
  }

  object FooNodeProcessor extends AbstractNodeProcessor("foo") {
    override def process(state: INodeMap, key: Any, foo: INode, engine: IFlagshipEngine): Unit = {
      val dictNode: INodeMap = engine.process(state, "dictionary", foo.attribute("dictionary"))
      val listNode: INodeMap = engine.process(state, "list", foo.attribute("list"))

      val newState = state
        .setAttribute("dictionary", Node(dictNode))
        .setAttribute("list", listNode.output)

      engine.process(newState, s"$name/bar", foo.attribute("bar"))
    }
  }

  object BarNodeProcessor extends AbstractNodeProcessor("foo/bar") {
    override def process(state: INodeMap, key: Any, bar: INode, engine: IFlagshipEngine): Unit =
      engine.process(state, s"$name/bat", bar.attribute("bat"))
  }

  object BatNodeProcessor extends AbstractNodeProcessor("foo/bar/bat") {
    override def process(state: INodeMap, key: Any, bat: INode, engine: IFlagshipEngine): Unit = {
      val dictNode = state("dictionary") // ("a" -> 1), ("b" -> 3)
      val listNode = state("list") // "world", "ignored", "bar"

      bat.keySet.foreach(batAttribute => { // "hello" | "foo"
        val batValue: String = bat.attributeAs[String](batAttribute) // "a" | "b"
        val index: Int = dictNode.attributeAs[Integer](batValue) // 1 | 3
        val listValue = listNode.index(index - 1).valueAs[String] // "world" | "bar"
        testResults = testResults :+ s"$batAttribute -> $listValue" // hello -> world | foo -> bar
      })
    }
  }

}
