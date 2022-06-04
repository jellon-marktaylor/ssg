package jellon.ssg.engine.flagship.st4

import jellon.ssg.engine.flagship.spi.IResolver
import jellon.ssg.engine.flagship.st4.ST4Resolver.{startChar, stopChar}
import jellon.ssg.engine.flagship.st4.helpers.{Groups, Templates}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.Node

import scala.annotation.tailrec

object ST4Resolver {
  private[this] val group = Groups.DEFAULT_GROUP
  private val startChar = group.delimiterStartChar
  private val stopChar = group.delimiterStopChar
}

class ST4Resolver(dictionary: INodeMap) extends IResolver {
  override def asText(rawText: String): String = {
    var previous = ""
    var rendered = rawText
    while (rendered != previous && rendered.contains(startChar) && rendered.contains(stopChar)) {
      previous = rendered // break the loop if st4 makes no changes to the string
      rendered = Templates.stringTemplate(dictionary, rendered)
        .render
    }

    rendered
  }

  @tailrec
  override final def asNode(path: String): INode =
    if (path.contains(startChar) && path.contains(stopChar) && path.indexOf(stopChar, 1) == (path.length - 1)) {
      asNode(path.substring(1, path.length - 1))
    } else if (path.contains('.')) {
      path.split("\\.")
        .foldLeft(Node(dictionary))((map, attr) => map.attribute(attr))
    } else {
      dictionary(path)
    }
}
