package jellon.ssg.engine.flagship.modules

import jellon.ssg.engine.flagship.api.IFlagshipApplication
import jellon.ssg.engine.flagship.integ.FlagshipHintHandlers
import jellon.ssg.engine.flagship.spi.IResolverFactory
import jellon.ssg.engine.flagship.st4.ST4ResolverFactory
import jellon.ssg.engine.flagship.{AbstractGuiceModule, FlagshipApplication}
import jellon.ssg.io.spi.IHintHandlers
import jellon.ssg.node.parser.api.INodeParsers
import jellon.ssg.node.parser.impl.DefaultNodeParsers

object FlagshipEngineModule extends AbstractGuiceModule {
  override def configure(): Unit = {
    // io
    bindTo[IHintHandlers, FlagshipHintHandlers]
    // parser
    bindTo[INodeParsers, DefaultNodeParsers]
    // engine
    bindTo[IFlagshipApplication, FlagshipApplication]
    bindTo[IResolverFactory](() => ST4ResolverFactory)
  }
}
