package jellon.ssg.node.parser.api

import jellon.ssg.node.api.INode

import java.io.IOException
import scala.util.Try

/** Aggregates multiple [[INodeParser]] into one */
trait INodeParsers {
  def apply(): Seq[INodeParser]

  @throws[IOException]
  def parse(resourceName: String): Option[INode] = this
    .apply()
    .to(LazyList)
    .filter(_.isDefinedAt(resourceName).getOrElse(true))
    .map(parser => Try(parser.parse(resourceName)))
    .filter(_.isSuccess)
    .map(_.get)
    .find(!INode.empty.equals(_))
}
