package jellon.ssg.node.parser.impl.xml

import jellon.ssg.io.impl.ClassLoaderInputStreamResources
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.{Node, NodeList}
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ListMap

object XmlParserTests {
  val subject: XmlParser =
    new XmlParser(ClassLoaderInputStreamResources(classOf[XmlParserTests]))

  val expectedNode: INode = Node(ListMap[Any, Any](
    "head" -> ListMap(
      "meta" -> ListMap("charset" -> "utf-8"),
      "title" -> "My XML Example"
    ),
    "body" -> ListMap(
      "img" -> ListMap(
        "src" -> "images/icon.png",
        "alt" -> "logo"
      ),
      "ol" -> ListMap(
        "li" -> Seq(
          // neither JSON nor YAML can imitate this
          new Node(Option("3"), new NodeList(Seq(Node("li3"))), INodeMap.empty),
          "li4",
          "li5",
        )
      )
    )
  ))
}

class XmlParserTests extends AnyFunSpec {

  import XmlParserTests._

  // TODO: this feature won't be available in the initial upload
  describe("apply(resourceName: String)") {
    ignore("should get the correct result for style1.xml") {
      val actual = subject.parse("style1.xml")
      assert(actual.toString === expectedNode.toString)
    }

    ignore("should get the correct result for style2.xml") {
      val actual = subject.parse("style2.xml")
      assert(actual.toString === expectedNode.toString)
    }

    ignore("should get the correct result for style3.xml") {
      val actual = subject.parse("style3.xml")
      assert(actual.toString === expectedNode.toString)
    }
  }
}
