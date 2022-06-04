package jellon.ssg.node.api

import jellon.ssg.node.spi.ValueNode
import org.scalatest.funspec.AnyFunSpec

object NodeExtTests {
}

class NodeExtTests extends AnyFunSpec {
  describe("INode.NodeExt.hasValue") {
    it("should be false for the INode.empty") {
      assert(!INode.empty.hasValue)
    }

    it("should be true for a ValueNode") {
      assert(new ValueNode(1).hasValue)
    }
  }

  describe("INode.NodeExt.optValueAs[String]") {
    it("should be empty for INode.empty") {
      assert(INode.empty.optValueAs[String].isEmpty)
    }

    it("should be empty for a ValueNode(Int)") {
      assert(new ValueNode(1).optValueAs[String].isEmpty)
    }

    it("should be defined for a String on ValueNode(String)") {
      assert(new ValueNode("1").optValueAs[String].isDefined)
    }
  }

  describe("INode.NodeExt.optValueAs[Integer]") {
    it("should be empty for INode.empty") {
      assert(INode.empty.optValueAs[Integer].isEmpty)
    }

    it("should be defined for a ValueNode(Int)") {
      assert(new ValueNode(1).optValueAs[Integer].isDefined)
    }

    it("should be empty for a ValueNode(String)") {
      assert(new ValueNode("1").optValueAs[Integer].isEmpty)
    }
  }

  describe("INode.NodeExt.optValueAs[Int]") {
    it("should be empty for a ValueNode(Int)") {
      assert(new ValueNode(1).optValueAs[Int].isEmpty)
    }
  }

  describe("INode.NodeExt.merge(INode)") {
    it("should combine two values into a Seq") {
      val node1 = new ValueNode(1)
      val node2 = new ValueNode(2)
      val actual = node1 ++ node2
      assert(actual.value === Seq(1, 2))
    }
  }
}
