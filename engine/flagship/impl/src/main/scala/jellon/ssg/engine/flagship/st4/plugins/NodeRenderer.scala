package jellon.ssg.engine.flagship.st4.plugins

import jellon.ssg.node.api.INode
import org.stringtemplate.v4.AttributeRenderer

import java.util.Locale

object NodeRenderer extends AttributeRenderer[INode] {
  override def toString(node: INode, formatString: String, locale: Locale): String = {
    val text = node.optValue match {
      case Some(value) =>
        value.toString
      case None =>
        node.toString
    }

    DirStringRenderer.toString(text, formatString, locale)
  }
}
