package jellon.ssg.engine.flagship.velocity

import jellon.ssg.engine.flagship.spi.IResolver
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ListMap

object VelocityResolverTests {
  val dictionary: INode = Node(ListMap(
    "package" -> "jellon.ssg.engine.flagship.velocity",
    "input" -> ListMap(
      "foo" -> "bar",
      "bar" -> "bat",
      "list" -> Seq(1, 2, 3)
    ),
    "instruction" -> ListMap(
      "bar" -> "${input.foo}",
      "bar-upper" -> "${instruction.bar.toUpperCase()}",
    ),
  ))

  val packageKey: String = "package"

  val packageValue: String = "jellon.ssg.engine.flagship.velocity"

  val packageAsDir: String = packageValue.replace('.', '/')

  val subject: IResolver = new VelocityResolver(dictionary)
}

class VelocityResolverTests extends AnyFunSpec {

  import VelocityResolverTests._

  describe("asText(rawText: String)") {
    it("should render a simple template") {
      val actual = subject.asText(s"$${$packageKey}")
      assertResult(packageValue)(actual)
    }

    it("should render a simple template with custom 'dir' formatting") {
      val actual = subject.asText(s"$${$packageKey.replace('.', '/')}")
      assertResult(packageAsDir)(actual)
    }

    it("should render lists") {
      val actual = subject.asText("${input.bar} #foreach( $li in $input.list )\n%${li}%#if( $foreach.hasNext ), #end#end")
      assertResult("bat %1%, %2%, %3%")(actual)
    }

    it("should render a node via toString") {
      val expected = "{foo=bar, bar=bat, list=[1, 2, 3]}"
      val actual = subject.asText("${input}")

      assertResult("{foo=bar, bar=bat, list=[1, 2, 3]}")(actual)
      assertResult("{ \"foo\": \"bar\", \"bar\": \"bat\", \"list\": [ 1, 2, 3 ] }")(dictionary.attribute("input").toString)
    }
  }

  describe("asNode(path: String)") {
    it("should resolve nodes") {
      val expected = dictionary.attribute("input").attributes("list")
      val actual = subject.asNode("${input.list}")
      assertResult(expected)(actual)
    }

    it("should not require open and close delimiters") {
      val expected = dictionary.attribute("input").attributes("list")
      val actual = subject.asNode("input.list")
      assertResult(expected)(actual)
    }

    it("should not resolve nodes recursively") {
      val expected = dictionary.attribute("instruction").attributes("bar")
      val actual = subject.asNode("${instruction.bar}")
      assertResult(expected)(actual)
      assertResult("${input.foo}")(actual.valueAs[String])
    }
  }
}
