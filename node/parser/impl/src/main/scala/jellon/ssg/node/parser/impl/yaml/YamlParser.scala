package jellon.ssg.node.parser.impl.yaml

import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.api.INode
import jellon.ssg.node.parser.impl.AbstractParser
import jellon.ssg.node.parser.impl.yaml.YamlParser.DEFAULT_PARSER
import jellon.ssg.node.spi.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject

@Component
class YamlParser(parser: Yaml, resourceReader: IInputStreamResources) extends AbstractParser {
  @Autowired
  @Inject
  def this(resourceReader: IInputStreamResources) = this(DEFAULT_PARSER, resourceReader)

  override def canParseResource(fileName: String): Boolean = {
    val lowerCaseFileName = fileName.toLowerCase
    lowerCaseFileName.endsWith(".yaml") ||
      lowerCaseFileName.endsWith(".yml")
  }

  override def apply(resourceName: String): INode = {
    val input = resourceReader.openInputStream(resourceName)
    try {
      val document: java.util.Map[Any, Any] = parser.load(input)
      INode(document.asInstanceOf[Any])
    } catch {
      case ex: RuntimeException =>
        ex.printStackTrace()
        throw ex
    }
  }
}

object YamlParser {
  private def DEFAULT_PARSER = new Yaml()

  def apply(parser: Yaml, resourceReader: IInputStreamResources) = new YamlParser(parser, resourceReader)

  def apply(resourceReader: IInputStreamResources) = new YamlParser(DEFAULT_PARSER, resourceReader)
}
