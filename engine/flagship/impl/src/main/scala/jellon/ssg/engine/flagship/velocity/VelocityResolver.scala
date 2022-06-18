package jellon.ssg.engine.flagship.velocity

import jellon.ssg.engine.flagship.spi.IResolver
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi.Node

import scala.annotation.tailrec

object VelocityResolver {
  private val startChar = "${"
  private val stopChar = "}"
}

class VelocityResolver(dictionary: INode) extends IResolver {

  import VelocityResolver._

  override def asText(rawText: String): String = {
    var previous = ""
    var rendered = rawText
    while (rendered != previous) {
      previous = rendered // break the loop if velocity makes no changes to the string
      rendered = merge(previous, dictionary)
    }

    rendered
  }

  @tailrec
  override final def asNode(path: String): INode =
    if (path.startsWith(startChar) && path.endsWith(stopChar)) {
      asNode(path.substring(2, path.length - 1))
    } else if (path.contains('.')) {
      path.split("\\.")
        .foldLeft(Node(dictionary))((map, attr) => map.attribute(attr))
    } else {
      dictionary.attribute(path)
    }
}
