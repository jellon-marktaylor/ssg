package jellon.ssg.io.spi

import jellon.ssg.io.IResetable
import jellon.ssg.io.impl._

import java.io.ByteArrayOutputStream

object ImplTestResources extends Resources(
  ClassLoaderUrlResources,
  ClassLoaderInputStreamResources,
  ByteArrayOutputStreamResources,
  new HintHandlers(Seq.empty)
)

class ImplTestResources(val urlResources: IUrlResources, val inputStreamResources: IInputStreamResources, val outputStreamResources: IOutputStreamResources, val hintHandlers: IHintHandlers)
  extends Resources(urlResources, inputStreamResources, outputStreamResources, hintHandlers) with IResetable {

  def this(outputStreamResources: IOutputStreamResources, hintHandlers: IHintHandlers) =
    this(ClassLoaderUrlResources, ClassLoaderInputStreamResources, outputStreamResources, hintHandlers)

  def this(outputStreamResources: IOutputStreamResources) =
    this(outputStreamResources, new HintHandlers(Seq.empty))

  def this(hintHandlers: IHintHandlers) =
    this(new ByteArrayOutputStreamResources(), hintHandlers)

  def this() =
    this(new HintHandlers(Seq.empty))

  def optTestOutputAsStream(resource: String): Option[ByteArrayOutputStream] = outputStreamResources match {
    case testOutputs: ByteArrayOutputStreamResources =>
      testOutputs.outputs.get(resource)
    case _ =>
      Option.empty
  }

  def optTestOutput(resource: String): Option[String] =
    optTestOutputAsStream(resource)
      .map(_.toString)

  def lookupTestOutputOrEmpty(resource: String): String =
    optTestOutputAsStream(resource)
      .map(_.toString)
      .getOrElse("")

  override def reset(): Unit = {
    IResetable.reset(urlResources)
    IResetable.reset(inputStreamResources)
    IResetable.reset(outputStreamResources)
    IResetable.reset(hintHandlers)
  }
}
