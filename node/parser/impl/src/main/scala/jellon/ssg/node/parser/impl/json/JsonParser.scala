package jellon.ssg.node.parser.impl.json

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.impl.AbstractParser
import jellon.ssg.node.parser.impl.json.JsonParser.{DEFAULT_JACKSON, jsonToNode}
import jellon.ssg.node.spi.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.inject.Inject
import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.IteratorHasAsScala

@Component
class JsonParser(jackson: ObjectMapper, resourceReader: IInputStreamResources) extends AbstractParser {
  @Autowired
  @Inject
  def this(resourceReader: IInputStreamResources) = this(DEFAULT_JACKSON, resourceReader)

  override def canParseResource(resourceName: String): Boolean = resourceName
    .toLowerCase()
    .endsWith(".json")

  def apply(resourceName: String): INode = {
    val input = resourceReader.openInputStream(resourceName)
    val json = jackson.readTree(input)
    jsonToNode(json)
  }
}

object JsonParser {
  private val DEFAULT_JACKSON = new ObjectMapper()

  def apply(resourceReader: IInputStreamResources): JsonParser = new JsonParser(DEFAULT_JACKSON, resourceReader)

  def apply(jackson: ObjectMapper, resourceReader: IInputStreamResources): JsonParser = new JsonParser(jackson, resourceReader)

  def jsonToNode(json: JsonNode): INode = {
    if (json.isArray) INode(listNode(json.elements()))
    else if (json.isObject) INode(mapNode(json.fields()))
    else if (json.isTextual) INode(json.textValue())
    else if (json.isNumber) INode(json.numberValue())
    else if (json.isBoolean) INode(json.booleanValue())
    else if (json.isNull) INode.empty
    else if (json.isBinary) INode(json.binaryValue())
    else throw new UnsupportedOperationException("Unknown or Unsupported JSON node type: " + json.getNodeType)
  }

  def listNode(nodes: java.util.Iterator[JsonNode]): Vector[INode] =
    nodes.asScala.map(node =>
      jsonToNode(node)
    ).toVector

  def mapNode(nodes: java.util.Iterator[java.util.Map.Entry[String, JsonNode]]): Map[String, INode] =
    nodes.asScala
      .map(entry =>
        (entry.getKey, jsonToNode(entry.getValue))
      )
      .to(ListMap)
}
