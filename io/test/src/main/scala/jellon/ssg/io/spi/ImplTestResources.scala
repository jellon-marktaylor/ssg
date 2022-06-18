package jellon.ssg.io.spi

import jellon.ssg.io.impl._

class ImplTestResources(val urlResources: IUrlResources, val inputStreamResources: IInputStreamResources, val outputStreamResources: ByteArrayOutputStreamResources, val hintHandlers: IHintHandlers) extends Resources(ClassLoaderUrlResources, ClassLoaderInputStreamResources, outputStreamResources, hintHandlers) {
  def this(outputStreamResources: ByteArrayOutputStreamResources, hintHandlers: IHintHandlers) =
    this(ClassLoaderUrlResources, ClassLoaderInputStreamResources, outputStreamResources, hintHandlers)

  def this(hintHandlers: IHintHandlers) =
    this(new ByteArrayOutputStreamResources(), hintHandlers)

  def this() =
    this(new HintHandlers(Seq.empty))

  def reset(): Unit =
    outputStreamResources.clear()
}
