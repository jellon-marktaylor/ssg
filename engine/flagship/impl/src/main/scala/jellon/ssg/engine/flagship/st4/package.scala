package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.st4.plugins.{DirStringRenderer, NodeModelAdapter, NodeRenderer}
import jellon.ssg.node.api.INode

import java.util.Locale

package object st4 {
  def getProperty(node: INode, property: Any): AnyRef =
    NodeModelAdapter.getProperty(node, property)

  def toString(value: INode, formatString: String, locale: Locale): String =
    NodeRenderer.toString(value, formatString, locale)

  def toString(value: String, formatString: String, locale: Locale): String =
    DirStringRenderer.toString(value, formatString, locale)
}
