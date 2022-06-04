package jellon.ssg.engine.flagship.st4

import jellon.ssg.engine.flagship.spi.{IResolver, IResolverFactory}
import jellon.ssg.node.api.INodeMap

object ST4ResolverFactory extends IResolverFactory {
  override def resolver(dictionary: INodeMap): IResolver =
    new ST4Resolver(dictionary)
}
