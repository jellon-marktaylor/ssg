package jellon.ssg.node.spi

import jellon.ssg.node.api.INodeMap.{NodeMapConverter, NodeMapExt}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.NodeMapTests._
import org.scalatest.funspec.AnyFunSpec

object NodeMapTests {
  val nodeMap: Map[Any, INode] = Map[Any, INode](
    0 -> Node(0),
    "key1" -> Node("node1"),
    "key2" -> Node("node2")
  )

  val nodeMapAlt: Map[Any, INode] = Map[Any, INode](
    "key3" -> Node("node3"),
    "key4" -> Node("node4")
  )

  val nodeMapOverlap: Map[Any, INode] = Map[Any, INode](
    "key2" -> Node("node3"),
    "key5" -> Node("node5")
  )

  val spareKey: Any = "key6"

  val spareNode: INode =
    Node(6)

  val subject: INodeMap =
    new NodeMap(nodeMap)
}

class NodeMapTests extends AnyFunSpec {
  describe("INodeMap.NodeMapConverter") {
    it("should toNodeMap: Map[Any, INode]") {
      val map: Map[Any, Any] = Map[Any, Any](
        0 -> 0,
        1 -> "1",
        "1" -> 2,
        "2" -> "3"
      )

      val actual: Map[Any, INode] = map.toNodeMap
      assert(actual(0) === Node(0))
      assert(actual(1) === Node("1"))
      assert(actual("1") === Node(2))
      assert(actual("2") === Node("3"))
    }
  }

  describe("INodeMap.NodeMapExt") {
    it("should toMap: Map[Any, INode]") {
      assert(subject.toMap === nodeMap)
    }

    it("should convert boxed primitives and objects with attributeAs[A: ClassTag](name: Any)") {
      assert(subject.attribute("foobar") === INode.empty)
      assert(subject.attributeAs[Integer](0) === 0)
      assert(subject.attributeAs[String]("key1") === "node1")
      assert(subject.attributeAs[String]("key2") === "node2")
    }

    it("should convert boxed primitives and objects with optAttributeAs[A: ClassTag](name: Any): Option[A]") {
      assert(subject.optAttributeAs("foobar").isEmpty)
      assert(subject.optAttributeAs[Integer](0).contains(0))
      assert(subject.optAttributeAs[String]("key1").contains("node1"))
      assert(subject.optAttributeAs[String]("key2").contains("node2"))
    }

    it("should setAttribute(kv: (Any, INode))") {
      val actual = subject.setAttribute(spareKey -> spareNode)
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === spareNode)
    }

    it("should setAttribute(key: Any, value: INode)") {
      val actual = subject.setAttribute(spareKey, spareNode)
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === spareNode)
    }

    it("should setAttribute(key: Any, value: Any)") {
      val actual = subject.setAttribute(spareKey, 6)
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === Node(6))
    }

    it("should replaceAttribute(key: Any, transform: INode => INode)") {
      val actual = subject
        .replaceAttribute(spareKey, node => node)
        .replaceAttribute(0, _ => spareNode)

      assert(actual.attribute(0) === spareNode)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === INode.empty)
    }

    it("should +(kv: (Any, INode))") {
      val kv: (Any, INode) = spareKey -> spareNode
      val actual = subject + kv
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === spareNode)
    }

    it("should ++(nodes: IterableOnce[(Any, INode)])") {
      val values: IterableOnce[(Any, INode)] = Seq(spareKey -> spareNode)
      val actual = subject ++ values
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attribute(spareKey) === spareNode)
    }

    it("should ++(nodes: INodeMap)") {
      val values: INodeMap = new NodeMap(nodeMapAlt)
      val actual = subject ++ values
      assert(actual.attributeAs[Integer](0) === 0)
      assert(actual.attributeAs[String]("key1") === "node1")
      assert(actual.attributeAs[String]("key2") === "node2")
      assert(actual.attributeAs[String]("key3") === "node3")
      assert(actual.attributeAs[String]("key4") === "node4")
    }
  }
}
