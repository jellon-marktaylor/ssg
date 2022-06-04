package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList, INodeMap}

import scala.jdk.OptionConverters.RichOptional

final class ValueNode(val optValue: Option[_]) extends INode {
  def this(optValue: java.util.Optional[_]) = this(optValue.toScala)

  def this(value: Any) = this(Option(value))

  override val children: INodeList = INodeList.empty

  override val attributes: INodeMap = INodeMap.empty

  override def equals(other: INode): Boolean =
    optValue == other.optValue

  override lazy val toString: String = optValue
    .map {
      case str: String => s"\"$str\""
      case other => other.toString
    }
    .getOrElse("null")
}
