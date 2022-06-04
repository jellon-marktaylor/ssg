package jellon.ssg.node.parser.impl

import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.api.INodeParser

abstract class AbstractParser extends INodeParser {
  @throws[java.io.IOException]
  override def parse(resourceName: String): INode =
    isDefinedAt(resourceName) match {
      case Some(false) => INode.empty
      case _ => this.apply(resourceName)
    }

  override def isDefinedAt(resourceName: String): Option[Boolean] =
    Option(canParseResource(resourceName))

  def canParseResource(resourceName: String): Boolean

  @throws[java.io.IOException]
  def apply(resourceName: String): INode
}
