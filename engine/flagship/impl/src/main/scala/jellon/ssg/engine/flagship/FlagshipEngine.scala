package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.INodeMap

class FlagshipEngine(processors: Map[String, Seq[INodeProcessor]], resolver: IResolverFactory, resources: IResources)
  extends AbstractFlagship(processors, resolver, resources)
    with IFlagshipEngine {
  override protected def engine: IFlagshipEngine = this

  override def process(name: String, state: INodeMap): INodeMap =
    super.process(name, state)
}
