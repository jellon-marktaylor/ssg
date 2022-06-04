package jellon.ssg.node.parser.impl

import jellon.ssg.io.impl.InputStreamResources
import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.{ListNode, Node}
import org.scalatest.funspec.AnyFunSpec

import java.io.File
import scala.collection.immutable.ListMap
import scala.io.Source

object DefaultNodeParsersTests {
  val basePath: String = {
    val path = "src/test/resources/jellon/ssg/node/parser/impl"
    if (new File("node/parser/test").isDirectory) s"node/parser/test/$path"
    else path
  }

  val resources: IInputStreamResources = new InputStreamResources(new File(basePath))

  val subject: DefaultNodeParsers = new DefaultNodeParsers(resources)

  val expectedNode: INode = Node(ListMap[Any, Any](
    "booleanValue" -> true,
    "numberValue" -> 2.1,
    "stringValue" -> "foo",
    "listNode" -> Seq(
      true,
      2.1,
      "foo",
      Seq(true, 2.1, "foo"),
      Seq(false, 3, "bar"),
      ListMap(
        "booleanValue" -> true,
        "numberValue" -> 2.1,
        "stringValue" -> "foo",
      ),
      ListMap(
        "booleanValue" -> false,
        "numberValue" -> 3,
        "stringValue" -> "bar",
      ),
    ),
    "mapNode" -> ListMap(
      "booleanValue" -> true,
      "numberValue" -> 2.1,
      "stringValue" -> "foo",
      "list1" -> Seq(true, 2.1, "foo"),
      "list2" -> Seq(false, 3, "bar"),
      "map1" -> ListMap(
        "booleanValue" -> true,
        "numberValue" -> 2.1,
        "stringValue" -> "foo",
      ),
      "map2" -> ListMap(
        "booleanValue" -> false,
        "numberValue" -> 3,
        "stringValue" -> "bar",
      ),
    )
  ))
}

class DefaultNodeParsersTests extends AnyFunSpec {

  import DefaultNodeParsersTests._

  describe("expected result") {
    it("should have the expected toString value") {
      val expectedText = Source.fromInputStream(resources.openInputStream("expected.txt"))
        .getLines()
        .mkString
      assert(expectedNode.toString === expectedText)
    }
  }

  describe("NodeParserFactory.parse(String)") {
    it("should parse JSON") {
      val actual = subject.parse("node.json")
      assert(actual.isDefined)
      assert(actual.get.toString === expectedNode.toString)
    }

    it("should parse YAML") {
      val actual = subject.parse("node.yaml")
      assert(actual.isDefined)
      assert(actual.get.toString === expectedNode.toString)
    }

    it("should parse YML") {
      val actual = subject.parse("node.yml")
      assert(actual.isDefined)
      assert(actual.get.toString === expectedNode.toString)
    }

    it("should parse XML") {
      // An XML list nested in another list will be parsed as a MapNode b/c the nested list will require elements
      val modifiedExpected = expectedNode.replaceAttribute("listNode", listNode => {
        val newNodeList = listNode.children.toSeq.map {
          case list: ListNode =>
            Node(Map[Any, Any]("elem" -> list))
          case childNode =>
            childNode
        }

        Node(newNodeList)
      })

      val actual = subject.parse("node.xml")
      assert(actual.isDefined)
      assert(actual.get.toString === modifiedExpected.toString)
    }
  }
}
