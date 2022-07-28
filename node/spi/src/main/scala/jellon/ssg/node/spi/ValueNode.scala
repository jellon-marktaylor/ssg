package jellon.ssg.node.spi

import jellon.ssg.node.api.{INode, INodeList, INodeMap}

import scala.jdk.OptionConverters.RichOptional

final class ValueNode(override val optValue: Option[_ <: AnyRef]) extends INode {
  def this(optValue: java.util.Optional[_ <: AnyRef]) = this(optValue.toScala)

  def this(value: AnyRef) = this(Option(value))

  override def asJava: Object =
    optValue
      .map {
        case anyRef: AnyRef =>
          anyRef
        case _ =>
          null
      }
      .orNull

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

object ValueNode {
  lazy val empty: ValueNode =
    new ValueNode(Option.empty)

  @inline
  def apply(optValue: Option[_]): ValueNode =
    new ValueNode(optValue)

  @inline
  def apply(value: AnyRef): ValueNode =
    new ValueNode(value)
}
