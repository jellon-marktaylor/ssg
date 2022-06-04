package jellon.ssg.engine.flagship.st4.plugins

import grizzled.slf4j.Logging
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.{Node, ValueNode}
import org.scalatest.funspec.AnyFunSpec

import java.util
import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.{MapHasAsJava, SeqHasAsJava}

class NodeModelAdapterTests extends AnyFunSpec with Logging {
  describe("NodeModelAdapter.getProperty(node: INode, property: Any)") {
    it("should return the value of a ValueNode(Int)") {
      val input: INode = Node(Map("ValueNode" -> new ValueNode(7)))
      val actual = NodeModelAdapter.getProperty(input, "ValueNode")
      assert(actual === 7)
    }

    it("should return the value of a ValueNode(String)") {
      val input: INode = Node(Map("ValueNode" -> new ValueNode("value")))
      val actual = NodeModelAdapter.getProperty(input, "ValueNode")
      assert(actual === "value")
    }

    it("should return an iterable[INode] of a ListNode") {
      val input: INode = Node(Map("ListNode" -> Seq(1, 2, 3)))
      val expected = input.attribute("ListNode").children.toSeq.asJava
      val actual = NodeModelAdapter.getProperty(input, "ListNode")
      assert(actual === expected)
    }

    it("should return an Map[_, INode] of a MapNode") {
      val input: INode = Node(Map("MapNode" -> ListMap("key1" -> "value1", "key2" -> "value2")))
      val expected = input.attribute("MapNode").attributes.toMap.asJava
      val actual = NodeModelAdapter.getProperty(input, "MapNode")
      assert(actual === expected)
    }
  }
}
