package jellon.ssg.node.parser.impl.xml

import grizzled.slf4j.Logging
import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.impl.AbstractParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.w3c.dom.Document

import javax.inject.Inject
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}

/**
 * The conversion from XML to Node will look very similar to converting the XML to JSON using
 * https://www.freeformatter.com/xml-to-json-converter.html#ad-output
 * and then parsing the JSON. There are some differences. For one, we won't use the '@'
 * it inserts, and our Nodes will have a value instead of an attribute where it would
 * print a name '#text'.
 * <br/>Excerpt:
 * <p>This process is not 100% accurate in that XML uses different item types that do not have an equivalent JSON representation.
 * <br/>The following rules will be applied during the conversion process:
 * <br/>Attributes will be treated as regular JSON properties
 * <br/>Attributes MAY be prefixed with a string to differentiate them from regular XML elements
 * <br/>Sequences of two or more similar elements will be converted to a JSON array
 * <br/>Namespaces are completely omitted from the resulting property names
 * <br/>You can add an attribute with name _type to element to infer the json type (boolean, float, integer, number, string)
 * <br/>Terminal #text item types will be converted into a JSON property with the name #text. This can be changed in the options.
 * </p>
 */
@Component
class XmlParser(parser: DocumentBuilder, resourceReader: IInputStreamResources) extends AbstractParser {

  @Autowired
  @Inject
  def this(resourceReader: IInputStreamResources) =
    this(XmlParser.DEFAULT_PARSER, resourceReader)

  override def canParseResource(resourceName: String): Boolean = resourceName
    .toLowerCase
    .endsWith(".xml")

  override def apply(resourceName: String): INode = {
    val input = resourceReader.openInputStream(resourceName)
    val document: Document = parser.parse(input)
    new DomNodePrinterExt(document).printNodeDetails()
    val value = new DomNodeExt(document.getDocumentElement).toNode
    value
      .attributes
      .toMap
      .head
      ._2
  }
}

object XmlParser {
  private def DEFAULT_PARSER: DocumentBuilder = DocumentBuilderFactory
    .newInstance()
    .newDocumentBuilder()

  def apply(parser: DocumentBuilder, resourceReader: IInputStreamResources): XmlParser =
    new XmlParser(parser, resourceReader)

  def apply(resourceReader: IInputStreamResources): XmlParser =
    new XmlParser(DEFAULT_PARSER, resourceReader)
}
