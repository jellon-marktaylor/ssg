package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.{IFlagshipApplication, INodeProcessors}
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.IResetable
import jellon.ssg.io.spi.{IResources, ImplTestResources}

import java.io.ByteArrayOutputStream

class FlagshipTestApplication(engine: FlagshipTestEngine) extends IFlagshipApplication with IResetable {
  def this(resources: IResources, resolver: IResolverFactory, processors: Seq[INodeProcessor]) =
    this(new FlagshipTestEngine(resources, resolver, processors))

  def this(resources: IResources, resolver: IResolverFactory, processors: INodeProcessors) =
    this(resources, resolver, processors.apply())

  /**
   * Uses default values. The IResources will implement IResetable and delegate the call to an instance of ByteArrayOutputStreamResources
   */
  def this() =
    this(new ImplTestResources, ResolverFactory, new NodeProcessors())

  override def createEngine: FlagshipTestEngine =
    engine

  override def reset(): Unit = {
    engine.reset()
  }

  def optTestOutputAsStream(resource: String): Option[ByteArrayOutputStream] =
    engine.optTestOutputAsStream(resource)

  def optTestOutput(resource: String): Option[String] =
    engine.optTestOutput(resource)

  def lookupTestOutputOrEmpty(resource: String): String =
    engine.lookupTestOutputOrEmpty(resource)
}
