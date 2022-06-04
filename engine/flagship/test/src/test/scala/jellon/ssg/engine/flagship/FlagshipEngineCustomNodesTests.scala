package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{BASE_PATH, IFlagshipNodeMapExtensions, instructionsNodeMap, outputNodeMap}
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.node.api.{INode, INodeList, INodeMap}
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ListMap

class FlagshipEngineCustomNodesTests extends AnyFunSpec {
  var output: Vector[String] = Vector.empty

  /** goal: add "hello -> world" and "foo -> bar" to output using custom node processors that demonstrate a variety of functionalities
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
    val model: INode = Node(ListMap(
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
      // defines the input list as a node called "output"
      ListNodeProcessor,
      // calls the bat node
      BarNodeProcessor,
      // write to `output` Vector; see scaladoc on `description` above
      BatNodeProcessor
    ))

    val handler = new FlagshipApplication(processors, null, null)
    handler.process(instructionsNodeMap(model))

    it("should only produce 2 outputs") {
      assert(output.size == 2)
    }

    it("should contain hello -> world") {
      assert(output.contains("hello -> world"))
    }

    it("should contain foo -> bar") {
      assert(output.contains("foo -> bar"))
    }
  }

  object RootNodeProcessor extends AbstractNodeProcessor(BASE_PATH) {
    override def processAttributes(root: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      engine.processInstructions("foo", state, root("foo"))
  }

  object ListNodeProcessor extends AbstractNodeProcessor("list") {
    override def processChildren(list: INodeList, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      outputNodeMap(Node(list))

    override def processAttributes(instructions: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      throw new UnsupportedOperationException("list shouldn't have attributes in this test method")
  }

  object DictionaryNodeProcessor extends AbstractNodeProcessor("dictionary") {
    override def processAttributes(dictionary: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      dictionary
  }

  object FooNodeProcessor extends AbstractNodeProcessor("foo") {
    def processAttributes(foo: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
      val dictNode: INodeMap = engine.processInstructions("dictionary", state, foo("dictionary"))
      val listNode: INodeMap = engine.processInstructions("list", state, foo("list"))

      val newState = state
        .setAttribute("dictionary", Node(dictNode))
        .setAttribute("list", listNode.output)

      engine.processInstructions(s"$path/bar", newState, foo("bar"))
    }
  }

  object BarNodeProcessor extends AbstractNodeProcessor("foo/bar") {
    override def processAttributes(bar: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap =
      engine.processInstructions(s"$path/bat", state, bar("bat"))
  }

  object BatNodeProcessor extends AbstractNodeProcessor("foo/bar/bat") {
    override def processAttributes(bat: INodeMap, state: INodeMap, engine: IFlagshipEngine): INodeMap = {
      val dictNode = state("dictionary") // ("a" -> 1), ("b" -> 3)
      val listNode = state("list") // "world", "ignored", "bar"

      bat.keySet.foreach(batAttribute => { // "hello" | "foo"
        val batValue: String = bat.attributeAs[String](batAttribute) // "a" | "b"
        val index: Int = dictNode.attributeAs[Integer](batValue) // 1 | 3
        val listValue = listNode.index(index - 1).valueAs[String] // "world" | "bar"
        output = output :+ s"$batAttribute -> $listValue" // hello -> world | foo -> bar
      })

      INodeMap.empty
    }
  }

}
