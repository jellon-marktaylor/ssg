package jellon.ssg.engine.flagship.velocity

import jellon.ssg.engine.flagship.velocity
import jellon.ssg.io.impl.ClassLoaderInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node
import org.scalatest.funspec.AnyFunSpec

import java.io.{ByteArrayOutputStream, StringWriter}

object PackageObjectTests {
  val nl: String = System.lineSeparator()

  val expectedOutput: String =
    s"hello = world${nl}foo = bar${nl}bar = bat$nl"

  val templateName: String =
    s"${classOf[PackageObjectTests].getPackage.getName.replace('.', '/')}/${classOf[PackageObjectTests].getSimpleName}.vm"

  val model: INode = Node(Map(
    "values" -> Seq(
      Map(
        "key" -> "hello",
        "value" -> "world",
      ),
      Map(
        "key" -> "foo",
        "value" -> "bar",
      ),
      Map(
        "key" -> "bar",
        "value" -> "bat",
      ),
    )
  ))
}

class PackageObjectTests extends AnyFunSpec {

  import PackageObjectTests._

  describe("merge(templateString: String, node: INode)") {
    it("should use a FlagshipVelocityContext to merge the template") {
      val actual = velocity.merge("${values[0].key} = ${values[1].value}", model)
      assertResult("hello = bar")(actual)
    }
  }

  describe("merge(inputResources: IInputStreamResources, templateName: String, node: INode, writer: Writer)") {
    it("should merge with a template") {
      val stringWriter = new StringWriter()
      velocity.merge(ClassLoaderInputStreamResources, templateName, model, stringWriter)
      val actual = stringWriter.toString
      assertResult(expectedOutput)(actual)
    }
  }

  describe("merge(inputResources: IInputStreamResources, templateName: String, node: INode, outputStream: OutputStream)") {
    it("should merge with a template") {
      val byteArrayOutputStream = new ByteArrayOutputStream()
      velocity.merge(ClassLoaderInputStreamResources, templateName, model, byteArrayOutputStream)
      val actual = byteArrayOutputStream.toString
      assertResult(expectedOutput)(actual)
    }
  }

  describe("merge(inputResources: IInputStreamResources, templateName: String, node: INode)") {
    it("should merge with a template") {
      val actual = velocity.merge(ClassLoaderInputStreamResources, templateName, model)
      assertResult(expectedOutput)(actual)
    }
  }
}
