package jellon.ssg.engine.flagship

import com.google.inject.{Guice, Injector}
import jellon.ssg.engine.flagship.api.IFlagshipApplication
import jellon.ssg.engine.flagship.api.IFlagshipEngine.{INPUT, INSTRUCTIONS}
import jellon.ssg.engine.flagship.modules.{FlagshipAppModule, FlagshipEngineModule, ProcessorsModule}
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.api.INodeParsers
import jellon.ssg.node.spi.NodeMap

object GuiceApp extends App {
  execute(
    System.getProperty("user.dir"),
    args
      .unapply(2)
      .getOrElse("."),
    args(0),
    args(1)
  )

  def createInjector(srcDir: String, outputDir: String): Injector =
    Guice.createInjector(
      new FlagshipAppModule(srcDir, outputDir),
      FlagshipEngineModule,
      ProcessorsModule
    )

  def execute(srcDir: String, outputDir: String, inputResource: String, instructionsResource: String): Unit = {
    execute(createInjector(srcDir, outputDir), inputResource, instructionsResource)
  }

  def execute(injector: Injector, inputResource: String, instructionsResource: String): Unit = {
    val parser = injector.getInstance[INodeParsers](classOf[INodeParsers])

    val input: INode = parser
      .parse(inputResource)
      .get

    val instructions: INode = parser
      .parse(instructionsResource)
      .get

    injector.getInstance[IFlagshipApplication](classOf[IFlagshipApplication])
      .process(new NodeMap(Map[Any, INode](
        INPUT -> input,
        INSTRUCTIONS -> instructions,
      )))
  }

  def execute(injector: Injector, input: INode, instructions: INode): Unit = {
    injector.getInstance[IFlagshipApplication](classOf[IFlagshipApplication])
      .process(new NodeMap(Map[Any, INode](
        INPUT -> input,
        INSTRUCTIONS -> instructions,
      )))
  }
}
