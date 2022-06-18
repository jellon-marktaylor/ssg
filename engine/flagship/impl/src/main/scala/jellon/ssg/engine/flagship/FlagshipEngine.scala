package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.{IFlagshipEngine, INodeProcessors}
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.{INode, INodeMap}

class FlagshipEngine(override val resources: IResources, override val resolver: IResolverFactory, processors: Seq[INodeProcessor])
  extends IFlagshipEngine {

  def this(resources: IResources, resolver: IResolverFactory, processors: INodeProcessors) =
    this(resources, resolver, processors.apply)

  override def process(state: INodeMap, key: Any, node: INode): INodeMap =
    processors
      .filter(_.handles(key, node))
      .foldLeft[INodeMap](state)((acc, processor) =>
        processor.apply(acc, key, node, this)
      )
}
