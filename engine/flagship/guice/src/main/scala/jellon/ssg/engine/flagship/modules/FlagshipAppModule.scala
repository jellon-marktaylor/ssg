package jellon.ssg.engine.flagship.modules

import jellon.ssg.engine.flagship.AbstractGuiceModule
import jellon.ssg.io.impl.{InputStreamResources, OutputStreamResources, Resources, UrlResources}
import jellon.ssg.io.spi.{IInputStreamResources, IOutputStreamResources, IResources, IUrlResources}

import java.io.File

/** Requires: IHintHandlers
 * Provides: IUrlResources, IInputStreamResources, IOutputStreamResources, IResources
 */
class FlagshipAppModule(srcDir: String, outputDir: String) extends AbstractGuiceModule {
  override def configure(): Unit = {
    bindTo[IUrlResources](() => new UrlResources(
      new File(srcDir)
    ))

    bindTo[IInputStreamResources](() => new InputStreamResources(
      new File(srcDir)
    ))

    bindTo[IOutputStreamResources](() => new OutputStreamResources(
      new File(outputDir)
    ))

    bindTo[IResources, Resources]
  }
}
