package jellon.ssg.engine.flagship.st4.helpers

import jellon.ssg.engine.flagship.st4.plugins.{DirStringRenderer, NodeModelAdapter, NodeRenderer}
import jellon.ssg.node.api.INode
import org.stringtemplate.v4.STGroup

object Groups {
  val DEFAULT_GROUP: STGroup =
    configureSTGroup(new STGroup('<', '>'))

  def configureSTGroup[A <: STGroup](group: A): A = {
    group.registerModelAdaptor(classOf[INode], NodeModelAdapter)

    group.registerRenderer(classOf[String], DirStringRenderer)
    group.registerRenderer(classOf[INode], NodeRenderer)

    group
  }
}
