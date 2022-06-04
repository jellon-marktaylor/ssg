package jellon.ssg.engine.flagship.st4

import jellon.ssg.engine.flagship.st4.ST4ResolverTests.packageKey
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

object ST4ResolverFactoryTests {
  val packageKeyNode: INode = Node(s"<$packageKey>")
}

class ST4ResolverFactoryTests extends AnyFunSpec {

  import ST4ResolverFactoryTests.packageKeyNode
  import ST4ResolverTests.{dictionary, packageKey, subject => resolver}

  describe("ST4ResolverFactory.resolver(dictionary: INodeMap)") {
    it("should return an instance of ST4Resolver") {
      val actual = ST4ResolverFactory.resolver(dictionary)
      assert(actual.isInstanceOf[ST4Resolver])
    }
  }

  describe("IResolverFactoryExt(ST4ResolverFactory).asNode(dictionary: INodeMap, packageKey: String)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asNode(packageKey)
      val actual = ST4ResolverFactory.asNode(dictionary, packageKey)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ST4ResolverFactory).asNode(dictionary: INodeMap, packageKeyNode: INode)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asNode(packageKeyNode)
      val actual = ST4ResolverFactory.asNode(dictionary, packageKeyNode)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ST4ResolverFactory).asText(dictionary: INodeMap, packageKey: String)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.asText(packageKey)
      val actual = ST4ResolverFactory.asText(dictionary, packageKey)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ST4ResolverFactory).resolveIfStringNode(dictionary: INodeMap, node: INode)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.resolveIfStringNode(packageKeyNode)
      val actual = ST4ResolverFactory.resolveIfStringNode(dictionary, packageKeyNode)
      assert(actual === expected)
    }
  }

  describe("IResolverFactoryExt(ST4ResolverFactory).resolveStringAttributes(dictionary: INodeMap, node: INodeMap)") {
    it("should return the same result as an ST4Resolver") {
      val expected = resolver.resolveStringAttributes(packageKeyNode.attributes)
      val actual = ST4ResolverFactory.resolveStringAttributes(dictionary, packageKeyNode.attributes)
      assert(actual === expected)
    }
  }
}
