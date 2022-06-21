package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.INodeProcessors
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.IResetable
import jellon.ssg.io.spi.{IResources, ImplTestResources}

import java.io.ByteArrayOutputStream

/**
 * Uses default values. The IResources will implement IResetable and delegate the call to an instance of ByteArrayOutputStreamResources
 */
object FlagshipTestEngine extends FlagshipEngine(ImplTestResources, ResolverFactory, FlagshipNodeProcessors)

class FlagshipTestEngine(resources: IResources, resolver: IResolverFactory, processors: Seq[INodeProcessor]) extends FlagshipEngine(resources, resolver, processors) with IResetable {
  def this(resources: IResources, resolver: IResolverFactory, processors: INodeProcessors) =
    this(resources, resolver, processors.apply())

  def this(processors: Seq[INodeProcessor]) =
    this(FlagshipTestEngine.resources, FlagshipTestEngine.resolver, processors)

  def this(processors: INodeProcessors) =
    this(processors.apply())

  /**
   * Uses default values. The IResources will implement IResetable and delegate the call to an instance of ByteArrayOutputStreamResources
   */
  def this() =
    this(new ImplTestResources, ResolverFactory, FlagshipNodeProcessors)

  def optTestOutputAsStream(resource: String): Option[ByteArrayOutputStream] = resources match {
    case testResources: ImplTestResources =>
      testResources.optTestOutputAsStream(resource)
    case _ =>
      Option.empty
  }

  def optTestOutput(resource: String): Option[String] = resources match {
    case testResources: ImplTestResources =>
      testResources.optTestOutput(resource)
    case _ =>
      Option.empty
  }

  def lookupTestOutputOrEmpty(resource: String): String = resources match {
    case testResources: ImplTestResources =>
      testResources.lookupTestOutputOrEmpty(resource)
    case _ =>
      ""
  }

  override def reset(): Unit = {
    IResetable.reset(resources)
    IResetable.reset(resolver)
    IResetable.reset(processors)
  }
}
