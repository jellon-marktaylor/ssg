package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship
import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.INodeMap

abstract class AbstractFlagship(protected val processorMap: Map[String, Seq[INodeProcessor]], val resolver: IResolverFactory, val resources: IResources) {
  def this(processors: Seq[INodeProcessor], resolver: IResolverFactory, resources: IResources) =
    this(processors.groupBy(_.path), resolver, resources)

  protected def engine: IFlagshipEngine

  protected def process(path: String, state: INodeMap): INodeMap =
    flagship.executeProcessors(processorMap, path, state, engine)
}
