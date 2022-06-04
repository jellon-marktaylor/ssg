package jellon.ssg.engine.flagship.st4.helpers

import jellon.ssg.node.api.INodeMap
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec
import org.stringtemplate.v4.{ST, STGroupDir, STGroupFile}

import scala.collection.immutable.ListMap

object TemplatesTests {
  val dictionary: INodeMap = Node(ListMap(
    "foo" -> "bar",
    "index" -> 1,
    "ignored" -> true
  )).attributes

  val path: String = "jellon/ssg/engine/flagship/st4/helpers"

  val file: String = "test.stg"

  val filePath: String = s"$path/$file"

  val templateName: String = "testTemplate"
}

class TemplatesTests extends AnyFunSpec {

  import jellon.ssg.engine.flagship.st4.helpers.TemplatesTests._

  describe("stringTemplate(template: String)") {
    it("should return an instance of ST") {
      val actual = Templates.stringTemplate("foobar")
      assert(actual.isInstanceOf[ST])
    }
  }

  describe("bind(attributes: INodeMap, template: ST)") {
    it("should find parameters in an anonymous template") {
      val actual = Templates.bind(dictionary, Templates.stringTemplate("<index> = <foo>"))
        .render()
      assert(actual === "1 = bar")
    }

    it("should find parameters in an STGroupFile template") {
      val groupFile = new STGroupFile(filePath)
      assert(groupFile != null, s"didn't find $file")

      val st = groupFile.getInstanceOf(templateName)
      assert(st != null, s"didn't find template '$templateName' in $file")

      val actual = Templates.bind(dictionary, st)
        .render()
      assert(actual === "1 = bar")
    }

    it("should find parameters in an STGroupDir template") {
      val groupDir = new STGroupDir(path)
      assert(groupDir != null, s"didn't find $file")

      val st = groupDir.getInstanceOf(s"/test/$templateName")
      assert(st != null, s"didn't find template '$templateName' in $file")

      val actual = Templates.bind(dictionary, st)
        .render()
      assert(actual === "1 = bar")
    }
  }
}
