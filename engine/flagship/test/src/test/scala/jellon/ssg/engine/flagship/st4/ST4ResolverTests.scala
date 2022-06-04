package jellon.ssg.engine.flagship.st4

import jellon.ssg.engine.flagship.spi.IResolver
import jellon.ssg.node.api.INodeMap
import jellon.ssg.node.spi.{Node, ValueNode}
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ListMap

object ST4ResolverTests {
  val dictionary: INodeMap = Node(ListMap(
    "package" -> "jellon.ssg.engine.flagship.st4",
    "input" -> ListMap(
      "foo" -> "bar",
      "bar" -> "bat",
      "list" -> Seq(1, 2, 3)
    ),
    "instruction" -> ListMap(
      "bar" -> "<input.foo>",
      "bar-upper" -> "<instruction.bar; format=\"upper\">",
    ),
  )).attributes

  val packageKey: String = "package"

  val packageValue: String = "jellon.ssg.engine.flagship.st4"

  val packageAsDir: String = packageValue.replace('.', '/')

  val subject: IResolver = new ST4Resolver(dictionary)
}

class ST4ResolverTests extends AnyFunSpec {

  import ST4ResolverTests._

  describe("asText(packageKey: String)") {
    it("should render a simple template") {
      val actual = subject.asText(s"<$packageKey>")
      assert(actual === packageValue)
    }

    it("should render a simple template with custom 'dir' formatting") {
      val actual = subject.asText(s"<$packageKey; format=\"dir\">")
      assert(actual === packageAsDir)
    }

    it("should render lists") {
      val actual = subject.asText("<input.bar> <input.list:{li | %<li>%}; separator={, }>")
      assert(actual === "bat %1%, %2%, %3%")
    }

    it("should render a node via toString") {
      val expected = "{ \"foo\": \"bar\", \"bar\": \"bat\", \"list\": [ 1, 2, 3 ] }"
      val actual = subject.asText("<input>")

      assert(dictionary.apply("input").toString === expected)
      assert(actual === expected)
    }
  }

  describe("asNode(packageKey: String)") {
    it("should resolve nodes") {
      val expected = dictionary("input").attributes("list")
      val actual = subject.asNode("<input.list>")
      assert(actual === expected)
    }
    it("should not require open and close delimiters") {
      val expected = dictionary("input").attributes("list")
      val actual = subject.asNode("input.list")
      assert(actual === expected)
    }

    it("should not resolve nodes recursively") {
      val expected = dictionary("instruction").attributes("bar")
      val actual = subject.asNode("<instruction.bar>")
      assert(actual === expected)
      assert(actual.valueAs[String] === "<input.foo>")
    }
  }

  describe("IResolverExt(ST4Resolver(...)).asNode(path: INode)") {
    it("should return the input for non-string ValueNodes") {
      val input = new ValueNode(1)
      val actual = subject.asNode(input)
      assert(actual === input)
    }

    it("should resolve text nodes as if we called asNode(path: String)") {
      val input = new ValueNode("instruction.bar")
      val expected = dictionary("instruction").attributes("bar")
      val actual = subject.asNode(input)
      assert(actual === expected)
    }
  }

  describe("IResolverExt(ST4Resolver(...)).resolveIfStringNode(node: INode)") {
    it("should return the input for non-string ValueNodes") {
      val input = new ValueNode(1)
      val actual = subject.resolveIfStringNode(input)
      assert(actual === input)
    }

    it("should resolve text nodes as if we called asText(path: String)") {
      // instruction.bar = "<input.foo>"
      // input.foo = "bar"
      val input = new ValueNode("<instruction.bar>")
      val expected = Node(subject.asText(input.valueAs[String]))
      val actual = subject.resolveIfStringNode(input)
      assert(actual === expected)
      assert(actual.valueAs[String] === "bar")
    }
  }

  describe("IResolverExt(ST4Resolver(...)).resolveStringAttributes(node: INodeMap)") {
    it("should resolve recursively") {
      val expected: INodeMap = Node(ListMap(
        "bar" -> "bar",
        "bar-upper" -> "BAR",
        "bar-indirect" -> "bar",
      )).attributes

      val input: INodeMap = Node(ListMap(
        "bar" -> "<input.foo>",
        "bar-upper" -> "<input.foo; format=\"upper\">",
        "bar-indirect" -> "<instruction.bar>",
      )).attributes

      val actual = subject.resolveStringAttributes(input)
      assert(actual === expected)
    }

    it("should ignore nested elements") {
      val expected: INodeMap = Node(ListMap(
        "nested" -> ListMap(
          "bar" -> "bar",
          "bar-indirect" -> "bar",
        ),
      )).attributes

      val actual = subject.resolveStringAttributes(expected)
      assert(actual === expected)
    }
  }
}
