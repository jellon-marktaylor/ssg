package jellon.ssg.engine.flagship

import com.google.inject.Guice
import jellon.ssg.engine.flagship.modules.{FlagshipEngineModule, ProcessorsModule}
import jellon.ssg.io.impl.{ByteArrayOutputStreamResources, ClassLoaderInputStreamResources, ClassLoaderUrlResources, Resources}
import jellon.ssg.io.spi.{IInputStreamResources, IOutputStreamResources, IResources, IUrlResources}
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.impl.json.JsonParser
import org.scalatest.funspec.AnyFunSpec

import scala.io.Source

class FlagshipEngineExample1Tests extends AnyFunSpec {

  object FlagshipEngineExample1TestsGuiceModule extends AbstractGuiceModule {
    val baosStreamResources: ByteArrayOutputStreamResources =
      new ByteArrayOutputStreamResources()

    override def configure(): Unit = {
      val path = IResources.relativeResourceOf[FlagshipEngineExample1Tests]
      bindTo[IUrlResources](() => ClassLoaderUrlResources(path))
      bindTo[IInputStreamResources](() => ClassLoaderInputStreamResources(path))
      bindTo[IOutputStreamResources](() => baosStreamResources)
      bindTo[IResources, Resources]
    }
  }

  describe("example1: custom processors (from engine/flagship/docs/user.md)") {
    it("should write the expected result") {
      val injector = Guice.createInjector(
        FlagshipEngineExample1TestsGuiceModule,
        FlagshipEngineModule,
        ProcessorsModule
      )

      val instructions: INode = injector.getInstance[JsonParser](classOf[JsonParser])
        .apply("instructions.json")

      GuiceApp.execute(injector, INode.empty, instructions)

      val output: Array[Byte] = FlagshipEngineExample1TestsGuiceModule
        .baosStreamResources
        .outputs("./my/custom/path/output.txt")
        .toByteArray

      val actual = Source.fromBytes(output)
        .getLines()
        .mkString

      assert(actual === "1 = bar")
    }
  }
}
