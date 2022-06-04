package jellon.ssg.engine.flagship.st4.plugins

import org.stringtemplate.v4.AttributeRenderer

import java.util.Locale

object DirStringRenderer extends AttributeRenderer[String] {
  override def toString(value: String, formatString: String, locale: Locale): String =
    if ("dir" == formatString) value.replace('.', '/')
    else ST4StringRenderer.toString(value, formatString, locale)
}
