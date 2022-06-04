package jellon.ssg.engine.flagship.st4.helpers

import jellon.ssg.engine.flagship.st4.helpers.Groups.DEFAULT_GROUP
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.{ListNode, ValueNode}
import org.stringtemplate.v4.ST

import java.io.IOException
import scala.jdk.CollectionConverters.{SeqHasAsJava, SetHasAsScala}

object Templates {
  def stringTemplate(template: String): ST =
    new ST(DEFAULT_GROUP, template)

  def stringTemplate(attributes: INodeMap, template: String): ST =
    bind(attributes, stringTemplate(template))

  def bind(attributes: INodeMap, template: ST): ST = {
    if (template.impl == null) {
      template
    } else {
      val args: Set[String] = findPropertiesInTemplate(template)
      args.foldLeft(template)((st1, arg) =>
        try {
          val node = attributes.optAttribute(arg) match {
            case Some(node) =>
              if (node == INode.empty) {
                throw new IOException(s"Undefined template parameter \"$arg\" in (${args.mkString(", ")}): ${template.impl.template}")
              } else {
                node
              }
            case _ =>
              throw new IOException(s"Undefined template parameter \"$arg\" in (${args.mkString(", ")}): ${template.impl.template}")
          }

          val value = node match {
            case valueNode: ValueNode =>
              valueNode.optValue.getOrElse(INode.empty)
            case listNode: ListNode =>
              // to get ST4 to treat a list as a list, this object must implement java.lang.Iterable
              listNode.elements.asJava
            case _ =>
              node
          }
          st1.add(arg, value)
        }
        catch {
          case rte: RuntimeException =>
            throw new IOException("Undefined template parameter <" + arg + "> in: " + template.impl.template, rte)
        }
      )
    }
  }

  private val LEFT_CURLY = 20 // magic number from Antlr parsing (can't be imported)

  private val RIGHT_CURLY = 21 // magic number from Antlr parsing (can't be imported)

  private val LEFT_DELIMITER = 23 // magic number from Antlr parsing (can't be imported)

  private def findPropertiesInTemplate(template: ST): Set[String] =
    if (template.impl.formalArguments == null)
      parsePropertiesInTemplate(template)
    else
      template.impl.formalArguments.keySet.asScala.toSet

  private def parsePropertiesInTemplate(template: ST): Set[String] = {
    var result = Set.empty[String]
    val tokens = template.impl.tokens
    var i = 0
    var nested = 0
    while (i < tokens.size) {
      val token = tokens.get(i)
      if (nested == 0 && (token.getType == LEFT_DELIMITER) && (i < tokens.size - 1)) {
        i += 1
        val attributeToken = tokens.get(i)
        result = result + attributeToken.getText
      } else if (token.getType == LEFT_CURLY) {
        nested += 1
      } else if (token.getType == RIGHT_CURLY) {
        nested -= 1
      }

      i += 1
    }

    result
  }
}
