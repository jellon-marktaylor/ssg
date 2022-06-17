package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.spi.{IResolver, IResolverFactory}
import jellon.ssg.engine.flagship.st4.ST4Resolver
import jellon.ssg.node.api.INodeMap

object ResolverFactory extends IResolverFactory {
  override def defaultResolver(dictionary: INodeMap): IResolver =
    new ST4Resolver(dictionary)

  override def namedResolver(name: String, dictionary: INodeMap): IResolver =
    defaultResolver(dictionary)
}
