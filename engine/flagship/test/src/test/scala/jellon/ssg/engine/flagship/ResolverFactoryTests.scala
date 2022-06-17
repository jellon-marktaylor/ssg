package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.st4.ST4ResolverTests.packageKey
import jellon.ssg.engine.flagship.st4.{ST4Resolver, ST4ResolverTests}
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object ResolverFactoryTests {
  val packageKeyNode: INode = Node(s"<$packageKey>")
}

class ResolverFactoryTests extends AnyFunSpec {

  import ResolverFactoryTests.packageKeyNode
  import ST4ResolverTests.{dictionary, packageKey, subject => resolver}

  describe("ResolverFactory.defaultResolver(dictionary: INodeMap)") {
    it("should return an instance of ST4Resolver") {
      val actual = ResolverFactory.defaultResolver(dictionary)
      assert(actual.isInstanceOf[ST4Resolver])
    }
  }

  describe("ResolverFactory.namedResolver(dictionary: INodeMap)") {
    it("should return an instance of ST4Resolver for st4") {
      val actual = ResolverFactory.namedResolver("st4", dictionary)
      assert(actual.isInstanceOf[ST4Resolver])
    }

    it("should be case-insensitive") {
      val actual = ResolverFactory.namedResolver("ST4", dictionary)
      assert(actual.isInstanceOf[ST4Resolver])
    }
  }

  describe("IResolverFactoryExt(ResolverFactory).asNode(dictionary: INodeMap, packageKey: String)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asNode(packageKey)
      val actual = ResolverFactory.asNode(dictionary, packageKey)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ResolverFactory).asNode(dictionary: INodeMap, packageKeyNode: INode)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asNode(packageKeyNode)
      val actual = ResolverFactory.asNode(dictionary, packageKeyNode)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ResolverFactory).asText(dictionary: INodeMap, packageKey: String)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asText(packageKey)
      val actual = ResolverFactory.asText(dictionary, packageKey)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ResolverFactory).resolveIfStringNode(dictionary: INodeMap, node: INode)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.resolveIfStringNode(packageKeyNode)
      val actual = ResolverFactory.resolveIfStringNode(dictionary, packageKeyNode)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ResolverFactory).resolveStringAttributes(dictionary: INodeMap, node: INodeMap)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.resolveStringAttributes(packageKeyNode.attributes)
      val actual = ResolverFactory.resolveStringAttributes(dictionary, packageKeyNode.attributes)
      assert(actual === expected)
    }
  }
}
