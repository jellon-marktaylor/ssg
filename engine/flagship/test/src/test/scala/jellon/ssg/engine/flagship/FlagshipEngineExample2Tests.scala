package jellon.ssg.engine.flagship

import com.google.inject.Guice
import jellon.ssg.engine.flagship.modules.{FlagshipEngineModule, ProcessorsModule}
import jellon.ssg.io.impl.{ByteArrayOutputStreamResources, ClassLoaderInputStreamResources, ClassLoaderUrlResources, Resources}
import jellon.ssg.io.spi.{IInputStreamResources, IOutputStreamResources, IResources, IUrlResources}
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.impl.json.JsonParser
import org.scalatest.funspec.AnyFunSpec

import scala.io.Source

class FlagshipEngineExample2Tests extends AnyFunSpec {

  object FlagshipEngineExample2TestsGuiceModule extends AbstractGuiceModule {
    val baosStreamResources: ByteArrayOutputStreamResources =
      new ByteArrayOutputStreamResources()

    override def configure(): Unit = {
      val path = IResources.relativeResource(this, "example2")
      bindTo[IUrlResources](() => ClassLoaderUrlResources(path))
      bindTo[IInputStreamResources](() => ClassLoaderInputStreamResources(path))
      bindTo[IOutputStreamResources](() => baosStreamResources)
      bindTo[IResources, Resources]
    }
  }

  describe("example2: generate maven pom.xml (from engine/flagship/docs/user.md)") {
    it("should write the expected result") {
      val injector = Guice.createInjector(
        FlagshipEngineExample2TestsGuiceModule,
        FlagshipEngineModule,
        ProcessorsModule
      )

      val expected: String = Source.fromInputStream(
        injector.getInstance[IInputStreamResources](classOf[IInputStreamResources])
          .openInputStream("auto-pom.xml")
      ).getLines().mkString

      val input: INode = injector.getInstance[JsonParser](classOf[JsonParser])
        .apply("project.json")

      val instructions: INode = injector.getInstance[JsonParser](classOf[JsonParser])
        .apply("instructions.json")

      GuiceApp.execute(injector, input, instructions)

      val output: Array[Byte] = FlagshipEngineExample2TestsGuiceModule
        .baosStreamResources
        .outputs("auto-pom.xml")
        .toByteArray

      val actual = Source.fromBytes(output)
        .getLines()
        .mkString

      assert(actual === expected)
    }
  }
}
