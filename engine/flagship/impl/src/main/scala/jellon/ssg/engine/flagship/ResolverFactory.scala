package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.spi.{IResolver, IResolverFactory}
import jellon.ssg.engine.flagship.st4.ST4Resolver
import jellon.ssg.engine.flagship.velocity.VelocityResolver
import jellon.ssg.node.api.INodeMap
import jellon.ssg.node.spi.MapNode

object ResolverFactory extends IResolverFactory {
  override def defaultResolver(dictionary: INodeMap): IResolver =
    new ST4Resolver(dictionary)

  override def namedResolver(name: String, dictionary: INodeMap): IResolver = name match {
    case "st4" =>
      new ST4Resolver(dictionary)
    case "velocity" =>
      new VelocityResolver(new MapNode(dictionary))
    case _ =>
      defaultResolver(dictionary)
  }
}
