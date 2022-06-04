package jellon.ssg.node.parser.impl

import grizzled.slf4j.Logging
import jellon.ssg.node.api.INode
import jellon.ssg.node.spi._
import org.w3c.dom.{NamedNodeMap, NodeList, Node => DomNode}

import java.text.{NumberFormat, ParsePosition}

package object xml extends Logging {
  private var indent: String = ""

  // value + value
  private def mergeNode(lhs: ValueNode, rhs: ValueNode): INode =
    INode.mergeOptions(lhs.optValue, rhs.optValue) match {
      case Some(value) => Node(value)
      case _ => INode.empty
    }

  // value + list
  private def mergeNode(lhs: ValueNode, rhs: ListNode): INode =
    Node(rhs.elements.prepended(lhs))

  private def mergeNode(lhs: ListNode, rhs: ValueNode): INode =
    Node(lhs.elements.appended(rhs))

  // value + map
  private def mergeNode(lhs: ValueNode, rhs: MapNode): INode =
    Node(Seq(lhs, rhs))

  private def mergeNode(lhs: MapNode, rhs: ValueNode): INode =
    Node(Seq(lhs, rhs))

  // list + list
  private def mergeNode(lhs: ListNode, rhs: ListNode): INode =
    Node(lhs.elements ++ rhs.elements)

  // list + map
  private def mergeNode(lhs: ListNode, rhs: MapNode): INode =
    Node(lhs.elements.appended(rhs))

  private def mergeNode(lhs: MapNode, rhs: ListNode): INode =
    Node(rhs.elements.prepended(lhs))

  // map + map
  private def mergeNode(lhs: MapNode, rhs: MapNode): INode = {
    val sharedKeys: Set[Any] = lhs.keySet.intersect(rhs.keySet)
    if (sharedKeys.isEmpty)
      lhs ++ rhs
    else {
      val result = rhs.elements.keySet.foldLeft[INode](lhs)((acc, key) => {
        if (sharedKeys.contains(key)) {
          val lhsAttribute = lhs.attribute(key)
          val rhsAttribute = rhs.attribute(key)
          val merged = mergeNodes(lhsAttribute, rhsAttribute)
          acc.setAttribute(key, merged)
        } else {
          val value = rhs.elements(key)
          acc.setAttribute(key, value)
        }
      })
      result
    }
  }

  /** @param lhs left-hand-side
   * @param rhs  right-hand-side
   * @return lhs ++ rhs (with special merge logic)
   */
  private def mergeNodes(lhs: INode, rhs: INode): INode = {
    lhs match {
      case lhsValueNode: ValueNode =>
        rhs match {
          case rhsValueNode: ValueNode =>
            mergeNode(lhsValueNode, rhsValueNode)
          case rhsListNode: ListNode =>
            mergeNode(lhsValueNode, rhsListNode)
          case rhsMapNode: MapNode =>
            mergeNode(lhsValueNode, rhsMapNode)
          case _ =>
            lhs ++ rhs
        }
      case lhsListNode: ListNode =>
        rhs match {
          case rhsValueNode: ValueNode =>
            mergeNode(lhsListNode, rhsValueNode)
          case rhsListNode: ListNode =>
            mergeNode(lhsListNode, rhsListNode)
          case rhsMapNode: MapNode =>
            mergeNode(lhsListNode, rhsMapNode)
          case _ =>
            lhs ++ rhs
        }
      case lhsMapNode: MapNode =>
        rhs match {
          case rhsValueNode: ValueNode =>
            mergeNode(lhsMapNode, rhsValueNode)
          case rhsListNode: ListNode =>
            mergeNode(lhsMapNode, rhsListNode)
          case rhsMapNode: MapNode =>
            mergeNode(lhsMapNode, rhsMapNode)
          case _ =>
            lhs ++ rhs
        }
      case _ =>
        lhs ++ rhs
    }
  }

  implicit class NamedNodeMapExt(self: NamedNodeMap) {
    def toSeq: Seq[DomNode] = Range(0, self.getLength)
      .map(i => self.item(i))
  }

  implicit class NodeListExt(self: NodeList) {
    def toSeq: Seq[DomNode] = Range(0, self.getLength)
      .map(i => self.item(i))
      .filterNot(_.isIgnorableWhitespace)
  }

  implicit class DomNodeExt[A <: DomNode](self: A) {
    private def asValue: Any =
      parseTextAsValue(self.getNodeValue)

    def isIgnorableWhitespace: Boolean =
      self.getNodeType == DomNode.TEXT_NODE &&
        self.getNodeName == "#text" &&
        self.getTextContent.trim.isEmpty

    def attributes: Seq[DomNode] =
      if (self.hasAttributes) self.getAttributes.toSeq
      else Seq.empty

    def childNodes: Seq[DomNode] =
      if (self.hasChildNodes) self.getChildNodes.toSeq
      else Seq.empty

    def nestedIn(node: INode): INode = {
      val nodeName = self.getNodeName

      val result = self.getNodeType match {
        case DomNode.ATTRIBUTE_NODE =>
          logger.trace(s"$indent${self.getTextContent} <= $nodeName (attribute)")
          node.setAttribute(nodeName, Node(asValue))
        case DomNode.TEXT_NODE =>
          logger.trace(s"$indent${self.getTextContent} <= $nodeName (text)")
          node.setValue(INode.mergeOptions(node.optValue, Some(asValue)))
        case DomNode.CDATA_SECTION_NODE =>
          logger.trace(s"$indent${self.getTextContent} <= $nodeName (cdata)")
          node.setValue(INode.mergeOptions(node.optValue, Some(asValue)))
        case DomNode.ELEMENT_NODE =>
          logger.trace(s"$indent<$nodeName>")
          indent = s"$indent  "
          try {
            var r = INode.empty
            r = self.attributes.foldLeft(r)((acc, node) =>
              node.nestedIn(acc)
            )
            r = self.childNodes.foldLeft(r)((acc, node) =>
              node.nestedIn(acc)
            )

            if (r == INode.empty) {
              logger.trace(s"$indent$nodeName <= $nodeName (element)")
              node.setAttribute(nodeName, Node(asValue))
            } else {
              node.optAttribute(nodeName) match {
                case Some(value) =>
                  val merged = mergeNodes(value, r)
                  logger.trace(s"$indent$merged <= $nodeName (element)")
                  node.setAttribute(nodeName, merged)
                case _ =>
                  logger.trace(s"$indent$r <= $nodeName (element)")
                  node.setAttribute(nodeName, r)
              }
            }
          } finally {
            indent = indent.replaceFirst("  ", "")
            logger.trace(s"$indent</$nodeName>")
          }
        case DomNode.COMMENT_NODE =>
          logger.trace(s"$indent<!-- ${self.getTextContent} --> <= (comment)")
          node
        case _ =>
          throw new IllegalArgumentException("Only document, element, attribute, text, and CDATA XML nodes are handled")
      }

      logger.trace(s"$indent$result")
      result
    }

    def toNode: INode =
      nestedIn(INode.empty)
  }

  private val TF: Set[String] = Set("true", "false")

  private val numberFormat: NumberFormat = NumberFormat.getInstance()

  private def tryParseBoolean(value: Any): Option[Any] =
    value match {
      case text: String =>
        if (TF.contains(text.toLowerCase)) Option(java.lang.Boolean.parseBoolean(text))
        else Option(text)
      case _ => Option(value)
    }

  private def tryParseNumber(value: Any): Option[Any] =
    value match {
      case text: String =>
        if (text.isEmpty || !Character.isDigit(text.charAt(0))) {
          Option(text)
        } else {
          val position = new ParsePosition(0)
          val number = numberFormat.parse(text, position)

          if (position.getIndex == text.length) Option(number)
          else Option(text)
        }
      case _ => Option(value)
    }

  private def parseTextAsValue(text: String): Any = Option(text)
    .flatMap(tryParseBoolean)
    .flatMap(tryParseNumber)
    .getOrElse(text)

  implicit class DomNodePrinterExt(self: DomNode) {
    def typeName: String = self.getNodeType match {
      case DomNode.ELEMENT_NODE => "Element"
      case DomNode.ATTRIBUTE_NODE => "Attr"
      case DomNode.TEXT_NODE => "Text"
      case DomNode.CDATA_SECTION_NODE => "CDATASection"
      case DomNode.ENTITY_REFERENCE_NODE => "EntityReference"
      case DomNode.ENTITY_NODE => "Entity"
      case DomNode.PROCESSING_INSTRUCTION_NODE => "ProcessingInstruction"
      case DomNode.COMMENT_NODE => "Comment"
      case DomNode.DOCUMENT_NODE => "Document"
      case DomNode.DOCUMENT_TYPE_NODE => "DocumentType"
      case DomNode.DOCUMENT_FRAGMENT_NODE => "DocumentFragment"
      case DomNode.NOTATION_NODE => "Notation"
    }

    def printNodeDetails(indent: String = ""): Unit = {
      val typeOfNode: String = typeName
      val nodeName = self.getNodeName

      self.getNodeType match {
        case DomNode.TEXT_NODE =>
          if (self.hasAttributes) {
            logger.trace(s"$indent<$typeOfNode name='$nodeName' value='${self.getNodeValue.replaceAll("\n", "\\\\n")}'>")
            Range(0, self.getAttributes.getLength)
              .map(self.getAttributes.item(_))
              .foreach(_.printNodeDetails(s"$indent  "))
            logger.trace(s"$indent</$typeOfNode>")
          } else {
            logger.trace(s"$indent<$typeOfNode name='$nodeName' value='${self.getNodeValue.replaceAll("\n", "\\\\n")}'/>")
          }
        case DomNode.ATTRIBUTE_NODE =>
          logger.trace(s"$indent<$typeOfNode name='$nodeName' value='${self.getNodeValue}'/>")
        case _ =>
          if (self.hasChildNodes) {
            logger.trace(s"$indent<$typeOfNode name='$nodeName'>")
            if (self.hasAttributes) {
              Range(0, self.getAttributes.getLength)
                .map(self.getAttributes.item(_))
                .foreach(_.printNodeDetails(s"$indent  "))
            }
            Range(0, self.getChildNodes.getLength)
              .map(self.getChildNodes.item(_))
              .foreach(_.printNodeDetails(s"$indent  "))
            logger.trace(s"$indent</$typeOfNode>")
          } else {
            if (self.hasAttributes) {
              logger.trace(s"$indent<$typeOfNode name='$nodeName'>")
              Range(0, self.getAttributes.getLength)
                .map(self.getAttributes.item(_))
                .foreach(_.printNodeDetails(s"$indent  "))
              logger.trace(s"$indent</$typeOfNode>")
            } else {
              logger.trace(s"$indent<$typeOfNode name='$nodeName'/>")
            }
          }
      }
    }
  }

}
