package jellon.ssg.node.spi

import jellon.ssg.node.api.INodeList.NodeIterableConverter
import jellon.ssg.node.api.{INode, INodeList}
import jellon.ssg.node.spi.NodeListTests.{nodeSeq, nodeSeqAlt, spareNode, subject}
import org.scalatest.funspec.AnyFunSpec

object NodeListTests {
  val nodeSeq: Seq[INode] = Seq(
    Node("node1"),
    Node("node2")
  )

  val nodeSeqAlt: Seq[INode] = Seq(
    Node("node3"),
    Node("node4")
  )

  val spareNode: INode =
    Node("node5")

  val subject: INodeList =
    new NodeList(nodeSeq)
}

class NodeListTests extends AnyFunSpec {
  describe("INodeList.NodeIterableConverter") {
    it("should toNodeIterator") {
      val seq: IterableOnce[Any] = Seq("node1", "node2")
      val actual: IterableOnce[INode] = seq.toNodeIterator.toSeq
      assert(actual === nodeSeq)
    }
  }

  //  describe("INodeList.NodeList") {
  //
  //  }

  describe("INodeList.NodeListExt") {
    it("should toSeq") {
      assert(subject.toSeq === nodeSeq)
    }

    it("should setChildren") {
      val actual = subject.setChildren(nodeSeqAlt)
      assert(actual.toSeq === nodeSeqAlt)
    }

    it("should addChild") {
      val actual = subject.addChild(spareNode)
      assert(actual.toSeq === nodeSeq :+ spareNode)
    }

    it("should :+") {
      val actual = subject :+ spareNode
      assert(actual.toSeq === nodeSeq :+ spareNode)
    }

    it("should addChildren") {
      val actual = subject.addChildren(nodeSeqAlt)
      assert(actual.toSeq === nodeSeq ++ nodeSeqAlt)
    }

    it("should ++(IterableOnce[INode])") {
      val actual = subject ++ nodeSeqAlt
      assert(actual.toSeq === nodeSeq ++ nodeSeqAlt)
    }

    it("should ++(INodeList)") {
      val actual = subject ++ new NodeList(nodeSeqAlt)
      assert(actual.toSeq === nodeSeq ++ nodeSeqAlt)
    }
  }
}
